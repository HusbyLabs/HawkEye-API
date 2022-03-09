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
import com.husbylabs.warptables.packets.FetchRequest;
import com.husbylabs.warptables.packets.FieldGrpc;
import com.husbylabs.warptables.packets.FieldResponse;
import com.husbylabs.warptables.packets.FieldUpdate;
import com.husbylabs.warptables.packets.FieldUpdatePost;
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.packets.SubscribeFieldRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class WTServer extends WarpTableInstance {

    private final Server server;
    private final Map<Integer, Integer> clients = new ConcurrentHashMap<>();
    private final Map<Integer, StreamObserver<FieldUpdate>> fieldStreamObservers = Maps.newHashMap();

    protected WTServer(InetSocketAddress address) {
        super(address);
        ServerBuilder<?> builder = ServerBuilder.forPort(address.getPort());
        builder.addService(new HandshakeImpl());
        builder.addService(new FieldImpl());
        server = builder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(WTServer.this::stop));
    }


    @Override
    public void start() throws IOException {
        if (status == Status.STARTED) {
            throw new IllegalStateException("The server is already started. Run .stop() before trying to start again.");
        }
        status = Status.STARTED;
        startPublishThread();
        server.start();
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
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
            status = Status.STOPPED;
        }
    }

    /**
     * Blocks util program is terminated
     *
     * @throws InterruptedException
     */
    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
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
    public Field getField(String path) {
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
        int handle = 0;
        while (fields.containsKey(handle)) {
            handle++;
        }
        fields.put(handle, leadingPath);
        parentTable.fields.put(base, new Field(handle, base, parentTable));
        return parentTable.fields.get(base);
    }

    @Override
    protected void publishQueuedField(Field field) {
        // TODO: IMPORTANT. ADD FIELD OWNERSHIP
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
        FieldUpdate payload = builder.build();
        fieldStreamObservers.forEach((key, value) -> value.onNext(payload));
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
            responseObserver.onNext(Constants.EMPTY);
            responseObserver.onCompleted();
        }
    }

    private class FieldImpl extends FieldGrpc.FieldImplBase {
        @Override
        public void fetch(FetchRequest request, StreamObserver<FieldResponse> responseObserver) {
            Field field = getField(request.getPath());
            FieldResponse response = FieldResponse.newBuilder()
                    .setHandle(field.getHandle())
                    .setPath(field.getPath())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void post(FieldUpdatePost request, StreamObserver<Empty> responseObserver) {
            synchronized (WTServer.this) {
                FieldUpdate update = request.getUpdate();
                Field field = getField(fields.get(update.getHandle()));
                String[] values = new String[update.getValueCount()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = update.getValue(i);
                }
                field.setValue(update.getType(), values);
                responseObserver.onNext(Constants.EMPTY);
                responseObserver.onCompleted();
                // TODO: IMPORTANT. ADD FIELD OWNERSHIP
                publishQueuedField(field);
            }
        }

        @Override
        public void subscribe(SubscribeFieldRequest request, StreamObserver<FieldUpdate> responseObserver) {
            fieldStreamObservers.put(request.getClientId(), responseObserver);
            ((ServerCallStreamObserver<FieldUpdate>) responseObserver).setOnCancelHandler(() -> unregisterSubscription(fieldStreamObservers, request.getClientId()));
        }
    }
}
