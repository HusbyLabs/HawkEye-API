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
import com.husbylabs.warptables.packets.FetchTableRequest;
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.packets.SubscribeTableRequest;
import com.husbylabs.warptables.packets.TableGrpc;
import com.husbylabs.warptables.packets.TableResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    protected WTServer(InetSocketAddress address) {
        super(address);
        ServerBuilder<?> builder = ServerBuilder.forPort(address.getPort());
        builder.addService(new HandshakeImpl());
        builder.addService(new TableImpl());
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
        if (tableIdByName.containsKey(name)) {
            return getTable(tableIdByName.get(name));
        } else {
            int x = 0;
            while (tablesById.containsKey(x)) {
                x++;
            }
            Table table = new Table(x, name);
            tablesById.put(x, table);
            tableIdByName.put(name, x);
            return table;
        }
    }

    private TableResponse createClientTable(FetchTableRequest request) {
        Table table = getTable(request.getName());
        TableResponse response = TableResponse.newBuilder()
                .setName(table.getName())
                .setTableId(table.getId())
                .build();
        tableStreamObservers.entrySet()
                .stream()
                .filter(e -> e.getKey() != request.getClientId())
                .forEach(e -> e.getValue().onNext(response));
        return response;
    }

    /*
     * Packet Handlers
     */
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
            TableResponse response;
            if (tableIdByName.containsKey(request.getName())) {
                Table table = getTable(request.getName());
                response = TableResponse.newBuilder()
                        .setName(table.getName())
                        .setTableId(table.getId())
                        .build();
            } else {
                response = createClientTable(request);
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void subscribe(SubscribeTableRequest request, StreamObserver<TableResponse> responseObserver) {
            // Send all current tables on subscribe
            for (Map.Entry<String, Integer> entry : tableIdByName.entrySet()) {
                TableResponse response = TableResponse.newBuilder()
                        .setName(entry.getKey())
                        .setTableId(entry.getValue())
                        .build();
                responseObserver.onNext(response);
            }
            tableStreamObservers.put(request.getClientId(), responseObserver);
        }
    }
}
