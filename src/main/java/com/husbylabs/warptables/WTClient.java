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
import com.husbylabs.warptables.packets.FetchRequest;
import com.husbylabs.warptables.packets.FieldGrpc;
import com.husbylabs.warptables.packets.FieldResponse;
import com.husbylabs.warptables.packets.FieldUpdate;
import com.husbylabs.warptables.packets.FieldUpdatePost;
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.packets.SubscribeFieldRequest;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.CallStreamObserver;

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

    private ManagedChannel channel;

    private int clientId = -1;

    private HandshakeGrpc.HandshakeBlockingStub handshakeStub;
    private FieldGrpc.FieldBlockingStub fieldStub;

    protected WTClient(InetSocketAddress address) {
        super(address);
    }

    /**
     * Start the WarpTable connection
     */
    @Override
    public void start() throws IOException {
        if (status != Status.STOPPED) {
            throw new IllegalStateException("The client is already started! Run .stop() before trying to start again.");
        }
        channel = ManagedChannelBuilder.forAddress(address.getHostName(), address.getPort())
                .usePlaintext()
                .build();
        setStatus(Status.STARTED);
        Thread connectionThread = new Thread(() -> {
            long lastAttempt = 0;
            while (status != Status.STOPPED) {
                if (status == Status.STARTED) {
                    setStatus(Status.AWAITING_CONNECTION);
                } else if (status == Status.AWAITING_CONNECTION && lastAttempt + 5000 < System.currentTimeMillis()) {
                    setStatus(Status.CONNECTING);
                    long timeout = System.currentTimeMillis() + 2000;
                    while (timeout > System.currentTimeMillis() && status != Status.CONNECTED) {
                        if (channel.getState(true) == ConnectivityState.READY) {
                            attemptHandshake();
                        }
                    }
                    if (status != Status.CONNECTED) {
                        lastAttempt = System.currentTimeMillis();
                        setStatus(Status.AWAITING_CONNECTION);
                    }
                }
            }
        });
        connectionThread.setDaemon(true);
        connectionThread.start();
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
            fieldStub = FieldGrpc.newBlockingStub(channel);
            handleClientConnection();
            setStatus(Status.CONNECTED);
            FieldGrpc.newStub(channel).subscribe(
                    SubscribeFieldRequest.newBuilder().setClientId(clientId).build(),
                    new FieldUpdateCallback()
            );
        }
    }

    private void handleClientConnection() {
        channel.notifyWhenStateChanged(channel.getState(false), () -> {
            ConnectivityState state = channel.getState(false);
            if (state != ConnectivityState.READY && status == Status.CONNECTED) {
                setStatus(Status.AWAITING_CONNECTION);
            }
            handleClientConnection();
        });
    }

    /**
     * Blocks current thread until the client is connected
     */
    public void awaitConnection() {
        while (status != Status.CONNECTED) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
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
        status = Status.STOPPED;
        channel.shutdown();
    }

    @Override
    public Field getField(String path) {
        if (status != Status.CONNECTED) {
            return null;
        }
        path = normalizePath(path, false);
        String leadingPath = "/" + path;
        String base = basePath(leadingPath);
        if (!path.contains("/")) {
            // TODO: Throw no root vars
        }
        String[] parts = path.split("/");
        Table parentTable = null;
        if (parts.length == 2) {
            parentTable = getTable(parts[0]);
        } else {
            for (int i = 0; i < parts.length - 1; i++) {
                if (parentTable == null) {
                    parentTable = getTable(parts[i]);
                } else {
                    parentTable = parentTable.getTable(parts[i]);
                }
            }
        }
        if (parentTable.hasField(base)) {
            return parentTable.getField(base);
        }
        FetchRequest request = FetchRequest.newBuilder()
                .setPath(leadingPath)
                .build();
        FieldResponse response = fieldStub.fetch(request);
        fields.put(response.getHandle(), response.getPath());
        parentTable.fields.put(base, new Field(response.getHandle(), base, parentTable));
        return parentTable.fields.get(base);
    }

    @Override
    public void postField(Field field) {
        if (status != Status.CONNECTED) {
            return;
        }
        new Thread(() -> {
            ArrayList<String> values = new ArrayList<>();
            if (field.getType().name().contains("ARRAY")) {
                Object[] arr = (Object[]) field.getValue();
                Arrays.stream(arr).forEach(o -> values.add(String.valueOf(o)));
            } else {
                values.add(String.valueOf(field.getValue()));
            }
            FieldUpdate.Builder builder = FieldUpdate.newBuilder()
                    .setHandle(field.getHandle())
                    .setType(field.getType());
            values.forEach(builder::addValue);
            FieldUpdatePost payload = FieldUpdatePost.newBuilder()
                    .setUpdate(builder.build())
                    .setClientId(clientId)
                    .build();
            fieldStub.post(payload);

        }).start();
    }

    private void registerStreamers() {
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

    private class FieldUpdateCallback extends Callback<FieldUpdate> {
        @Override
        public void onNext(FieldUpdate value) {
            Field field = getField(fields.get(value.getHandle()));
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
