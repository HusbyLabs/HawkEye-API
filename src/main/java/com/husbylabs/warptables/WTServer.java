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

import com.google.common.collect.Maps;
import com.google.protobuf.Empty;
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
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class WTServer extends WarpTableInstance {

    private final Server server;
    private final Map<Integer, Integer> clients = Maps.newHashMap();
    private final Map<Integer, StreamObserver<TableResponse>> tableStreamObservers = Maps.newHashMap();
    private final Map<Integer, StreamObserver<FieldUpdate>> fieldStreamObservers = Maps.newHashMap();

    protected WTServer(InetSocketAddress address) {
        super(address);
        ServerBuilder<?> builder = ServerBuilder.forPort(address.getPort());
        builder.addService(new HandshakeImpl());
        builder.addService(new TableImpl());
        builder.addService(new FieldImpl());
        server = builder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(WTServer.this::stop));
    }


    @Override
    public void start() throws IOException {
        WarpTablesAPI.getLogger().info("Starting WarpTable server");
        server.start();
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Creates a new client
     *
     * @param protocol The protocol version of the client
     * @return The ID of the new client
     */
    protected int createNewClient(int protocol) {
        int x = 0;
        while (clients.containsKey(x)) {
            x++;
        }
        clients.put(x, protocol);
        return x;
    }

    @Override
    public Table getTable(String name) {
        return getTable(name, -1);
    }

    /**
     * Gets a {@link Table} from the name. It will create a table if it does not exist.
     *
     * @param name     Name of table
     * @param clientId Id of client
     * @return {@link Table}
     */
    private Table getTable(String name, int clientId) {
        if (tablesByName.containsKey(name)) {
            return getTable(tablesByName.get(name));
        } else {
            int x = 0;
            while (tables.containsKey(x)) {
                x++;
            }
            Table table = new Table(x, name, this);
            tables.put(x, table);
            tablesByName.put(name, x);
            if (clientId != -1) {
                TableResponse response = TableResponse.newBuilder()
                        .setName(table.getName())
                        .setTableId(table.getId())
                        .build();
                tableStreamObservers.entrySet()
                        .stream()
                        .filter(e -> e.getKey() != clientId)
                        .forEach(e -> e.getValue().onNext(response));
            }
            return table;
        }
    }

    @Override
    public Field getField(int tableId, int fieldId) {
        Table table = getTable(tableId);
        if (table == null) {
            return null;
        }
        return table.getField(fieldId);
    }

    @Override
    public Field getField(int tableId, String name) {
        Table table = getTable(tableId);
        if (table == null) {
            return null;
        }
        Field field;
        if (table.fieldsByName.containsKey(name)) {
            field = table.getField(table.fieldsByName.get(name));
        } else {
            int x = 0;
            while (table.fields.containsKey(x)) {
                x++;
            }
            field = new Field(x, name, table);
            table.fields.put(x, field);
            table.fieldsByName.put(name, x);
        }
        return field;
    }

    @Override
    public void postField(Field field) {
        postField(field, -1);
    }

    private void postField(Field field, int clientId) {
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
        FieldUpdate payload = builder.build();
        fieldStreamObservers.entrySet()
                .stream()
                .filter(e -> e.getKey() != clientId)
                .forEach(e -> e.getValue().onNext(payload));

    }

    /*
     * Packet Handlers
     */

    private void unregisterSubscription(Map<Integer, ?> observer, int clientId) {
        observer.remove(clientId);
    }

    private class HandshakeImpl extends HandshakeGrpc.HandshakeImplBase {
        @Override
        public void initiateHandshake(ClientHandshake request, StreamObserver<ServerHandshake> responseObserver) {
            int clientProtocolVersion = request.getProtocol();
            boolean supported = Arrays.asList(Constants.COMPATIBLE_PROTOCOL_VERSIONS).contains(clientProtocolVersion);
            int clientId = supported ? createNewClient(clientProtocolVersion) : -1;
            ServerHandshake handshake = ServerHandshake.newBuilder()
                    .setProtocol(Constants.PROTO_VER)
                    .setSupported(supported)
                    .setClientId(clientId)
                    .build();
            responseObserver.onNext(handshake);
            responseObserver.onCompleted();
        }

        @Override
        public void heartbeat(Empty request, StreamObserver<Empty> responseObserver) {
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    private class TableImpl extends TableGrpc.TableImplBase {
        @Override
        public void fetch(FetchTableRequest request, StreamObserver<TableResponse> responseObserver) {
            Table table = getTable(request.getName(), request.getClientId());
            TableResponse response = TableResponse.newBuilder()
                    .setName(table.getName())
                    .setTableId(table.getId())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void subscribe(SubscribeTableRequest request, StreamObserver<TableResponse> responseObserver) {
            // Send all current tables on subscribe
            for (Map.Entry<String, Integer> entry : tablesByName.entrySet()) {
                TableResponse response = TableResponse.newBuilder()
                        .setName(entry.getKey())
                        .setTableId(entry.getValue())
                        .build();
                responseObserver.onNext(response);
            }
            tableStreamObservers.put(request.getClientId(), responseObserver);
            ((ServerCallStreamObserver<TableResponse>) responseObserver).setOnCancelHandler(() -> unregisterSubscription(tableStreamObservers, request.getClientId()));
        }
    }

    private class FieldImpl extends FieldGrpc.FieldImplBase {
        @Override
        public void fetchField(FetchFieldRequest request, StreamObserver<FieldResponse> responseObserver) {
            Field field = getField(request.getTableId(), request.getName());
            FieldResponse response = FieldResponse.newBuilder()
                    .setFieldId(field.getId())
                    .setName(field.getName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void fetchFieldById(FetchFieldIdRequest request, StreamObserver<FieldResponse> responseObserver) {
            Field field = getField(request.getTableId(), request.getFieldId());
            if (field == null) {
                field = new Field(-1, "", null);
            }
            FieldResponse response = FieldResponse.newBuilder()
                    .setName(field.getName())
                    .setTableId(request.getTableId())
                    .setFieldId(field.getId())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void post(FieldUpdatePost request, StreamObserver<Empty> responseObserver) {
            FieldUpdate update = request.getUpdate();
            Field field = getField(update.getTableId(), update.getFieldId());
            String[] values = new String[update.getValueCount()];
            for (int i = 0; i < values.length; i++) {
                values[i] = update.getValue(i);
            }
            field.setValue(update.getType(), values);
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
            postField(field, request.getClientId());
        }

        @Override
        public void subscribe(SubscribeFieldRequest request, StreamObserver<FieldUpdate> responseObserver) {
            fieldStreamObservers.put(request.getClientId(), responseObserver);
            ((ServerCallStreamObserver<FieldUpdate>) responseObserver).setOnCancelHandler(() -> unregisterSubscription(fieldStreamObservers, request.getClientId()));
        }
    }
}
