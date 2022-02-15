/*
 * MIT License
 *
 * Copyright (c) 2022 Husby Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 */

package com.husbylabs.warptables;

import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.FetchFieldIdRequest;
import com.husbylabs.warptables.packets.FetchFieldRequest;
import com.husbylabs.warptables.packets.FetchTableRequest;
import com.husbylabs.warptables.packets.FieldGrpc;
import com.husbylabs.warptables.packets.FieldResponse;
import com.husbylabs.warptables.packets.FieldUpdate;
import com.husbylabs.warptables.packets.FieldUpdatePost;
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.packets.SubscribeFieldRequest;
import com.husbylabs.warptables.packets.SubscribeTableRequest;
import com.husbylabs.warptables.packets.TableGrpc;
import com.husbylabs.warptables.packets.TableResponse;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.CallStreamObserver;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * The WarpTables client for writing and reading data
 *
 * @author Noah Husby
 */
public class WTClient extends WarpTableInstance {

    private final ManagedChannel channel;
    @Getter
    private boolean connected = false;
    private boolean autoConnect = false;
    private boolean started = false;
    private Thread autoConnectThread;

    private int clientId = -1;

    private HandshakeGrpc.HandshakeBlockingStub handshakeStub;
    private TableGrpc.TableBlockingStub tableStub;
    private FieldGrpc.FieldBlockingStub fieldStub;

    protected WTClient(InetSocketAddress address) {
        super(address);
        channel = ManagedChannelBuilder.forAddress(address.getHostName(), address.getPort())
                .usePlaintext()
                .build();
    }

    /**
     * Start the WarpTable connection
     */
    @Override
    public void start() throws IOException, InterruptedException {
        long timeout = System.currentTimeMillis() + 5000;
        started = true;
        // Attempt 5000ms blocking connection
        while (timeout > System.currentTimeMillis() && !connected) {
            if (channel.getState(true) == ConnectivityState.READY) {
                attemptHandshake();
            }
        }
        if (connected) {
            return;
        }

        // Error handling depending on autoConnect option
        if (autoConnect) {
            handleAutoConnect();
        } else {
            throw new IOException("The WarpTables server is not accessible.");
        }
    }

    private void handleAutoConnect() {
        if (autoConnectThread != null && autoConnectThread.isAlive()) {
            return;
        }
        autoConnectThread = new Thread(() -> {
            while (!connected) {
                if (channel.getState(true) == ConnectivityState.READY) {
                    attemptHandshake();
                }
            }
        });
        autoConnectThread.start();
    }

    /**
     * Attempts a client -> server handshake
     */
    private void attemptHandshake() {
        handshakeStub = HandshakeGrpc.newBlockingStub(channel);
        ClientHandshake clientHandshake = ClientHandshake.newBuilder()
                .setProtocol(Constants.PROTO_VER)
                .build();
        ServerHandshake serverHandshake = handshakeStub.initiateHandshake(clientHandshake);
        if (serverHandshake.getSupported()) {
            clientId = serverHandshake.getClientId();
            System.out.println("Connected w/ Client Id: " + clientId);
            tableStub = TableGrpc.newBlockingStub(channel);
            fieldStub = FieldGrpc.newBlockingStub(channel);
            handleClientConnection();
            connected = true;
            new Thread(this::registerStreamers).start();
        }
    }

    private void handleClientConnection() {
        channel.notifyWhenStateChanged(channel.getState(false), () -> {
            System.out.println("State change: " + channel.getState(false));
            if (channel.getState(false) != ConnectivityState.READY && connected) {
                connected = false;
                if (autoConnect && started) {
                    handleAutoConnect();
                }
            }
            handleClientConnection();
        });
    }

    /**
     * Enable the auto connect feature.
     * The auto connect feature will attempt to keep the client and server connected as long as {@link #stop()} hasn't been called.
     */
    public void enableAutoConnect() {
        autoConnect = true;
    }

    /**
     * Disable the auto connect feature.
     */
    public void disableAutoConnect() {
        autoConnect = false;
    }

    /**
     * Blocks current thread until the client is connected
     */
    public void awaitConnection() {
        while (!connected) {
        }
    }

    public void awaitTermination() {
        try {
            channel.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        WarpTablesAPI.getLogger().info("Stopping WarpTable client");
        autoConnectThread.interrupt();
        autoConnectThread = null;
        started = false;
    }

    @Override
    public Table getTable(String tableName) {
        // Check server for table information
        if (!tablesByName.containsKey(tableName)) {
            FetchTableRequest tableRequest = FetchTableRequest.newBuilder()
                    .setClientId(clientId)
                    .setName(tableName)
                    .build();
            TableResponse tableResponse = tableStub.fetch(tableRequest);
            return handleTable(tableResponse.getName(), tableResponse.getTableId());
        }
        return getTable(tablesByName.get(tableName));
    }

    @Override
    public Field getField(int tableId, int fieldId) {
        Table table = tables.get(tableId);
        if (table == null) {
            return null;
        }
        Field field = table.getField(fieldId);
        if (field == null) {
            FetchFieldIdRequest request = FetchFieldIdRequest.newBuilder()
                    .setTableId(tableId)
                    .setFieldId(fieldId)
                    .build();
            FieldResponse response = fieldStub.fetchFieldById(request);
            if (response.getFieldId() == -1) {
                return null;
            }
            field = new Field(response.getFieldId(), response.getName(), table);
            table.fields.put(response.getFieldId(), field);
            table.fieldsByName.put(response.getName(), response.getFieldId());
        }
        return field;
    }

    @Override
    public Field getField(int tableId, String name) {
        Table table = getTable(tableId);
        if (table == null) {
            return null;
        }
        if (!table.fieldsByName.containsKey(name)) {
            FetchFieldRequest request = FetchFieldRequest.newBuilder()
                    .setName(name)
                    .setTableId(tableId)
                    .build();
            FieldResponse response = fieldStub.fetchField(request);
            Field field = new Field(response.getFieldId(), response.getName(), table);
            table.fields.put(field.getId(), field);
            table.fieldsByName.put(field.getName(), field.getId());
            return field;
        }
        return table.fields.get(table.fieldsByName.get(name));
    }

    @Override
    public void postField(Field field) {
        ArrayList<String> values = new ArrayList<>();
        if (field.getType().name().contains("ARRAY")) {
            Object[] arr = (Object[]) field.getValue();
            Arrays.stream(arr).forEach(o -> values.add(String.valueOf(o)));
        } else {
            values.add(String.valueOf(field.getValue()));
        }
        FieldUpdate.Builder builder = FieldUpdate.newBuilder()
                .setTableId(field.getTable().getId())
                .setFieldId(field.getId())
                .setType(field.getType());
        values.forEach(builder::addValue);
        FieldUpdatePost payload = FieldUpdatePost.newBuilder()
                .setUpdate(builder.build())
                .setClientId(clientId)
                .build();
        fieldStub.post(payload);
    }

    private void registerStreamers() {
        TableGrpc.newStub(channel).subscribe(
                SubscribeTableRequest.newBuilder().setClientId(clientId).build(),
                new TableResponseCallback()
        );
        FieldGrpc.newStub(channel).subscribe(
                SubscribeFieldRequest.newBuilder().setClientId(clientId).build(),
                new FieldUpdateCallback()
        );
    }

    /*
     * Callbacks
     */

    private abstract static class Callback<T> extends CallStreamObserver<T> {

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setOnReadyHandler(Runnable onReadyHandler) {

        }

        @Override
        public void disableAutoInboundFlowControl() {

        }

        @Override
        public void request(int count) {

        }

        @Override
        public void setMessageCompression(boolean enable) {

        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {

        }
    }

    private class TableResponseCallback extends Callback<TableResponse> {

        @Override
        public void onNext(TableResponse value) {
            Table temp = getTable(value.getTableId());
            if (temp != null && temp.getName().equalsIgnoreCase(value.getName())) {
                return;

            }
            handleTable(value.getName(), value.getTableId());
            System.out.printf("[Client: %s, Table Name: %s, Table ID: %s]\n", clientId, value.getName(), value.getTableId());
        }
    }

    private class FieldUpdateCallback extends Callback<FieldUpdate> {
        @Override
        public void onNext(FieldUpdate value) {
            Field field = getField(value.getTableId(), value.getFieldId());
            if (field == null) {
                System.out.println("ERROR!");
                return;
            }
            String[] values = new String[value.getValueCount()];
            for (int i = 0; i < values.length; i++) {
                values[i] = value.getValue(i);
            }
            field.setValue(value.getType(), values);
        }
    }
}
