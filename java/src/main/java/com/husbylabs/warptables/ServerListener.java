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

import com.google.protobuf.Empty;
import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class ServerListener {
    private final WTServer wtServer;

    public void listen(ServerBuilder<?> builder) {
        builder.addService(new HandshakeImpl());
    }

    private static class HandshakeImpl extends HandshakeGrpc.HandshakeImplBase {
        @Override
        public void initiateHandshake(ClientHandshake request, StreamObserver<ServerHandshake> responseObserver) {
            ServerHandshake handshake = ServerHandshake.newBuilder()
                    .setProtocol(Constants.PROTO_VER)
                    .setSupported(Arrays.asList(Constants.COMPATIBLE_PROTOCOL_VERSIONS).contains(Constants.PROTO_VER))
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
}
