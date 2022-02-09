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
import com.husbylabs.warptables.packets.HandshakeGrpc;
import com.husbylabs.warptables.packets.ServerHandshake;
import io.grpc.ConnectivityState;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

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
        // Attempt 5000ms blocking connection
        while (timeout > System.currentTimeMillis() && !connected) {
            if(channel.getState(true) == ConnectivityState.READY) {
                attemptHandshake();
            }
        }
        if(connected) {
            return;
        }

        // Error handling depending on autoConnect option
        if(autoConnect) {
            //TODO: Handle auto connect
        } else {
            throw new IOException("The WarpTables server is not accessible.");
        }
    }

    private void handleAutoConnect() {

    }

    private void attemptHandshake() {
        HandshakeGrpc.HandshakeBlockingStub stub = HandshakeGrpc.newBlockingStub(channel);
        ClientHandshake clientHandshake = ClientHandshake.newBuilder()
                .setProtocol(Constants.PROTO_VER)
                .build();
        ServerHandshake serverHandshake = stub.initiateHandshake(clientHandshake);
        if(serverHandshake.getSupported()) {
            connected = true;
        }
    }

    public void enableAutoConnect() {
        autoConnect = true;
    }

    public void disableAutoConnect() {
        autoConnect = false;
    }

    public void awaitConnection() {
        while (!connected) {}
    }

    @Override
    public void stop() {
        WarpTablesAPI.getLogger().info("Stopping WarpTable client");
    }

    @Override
    public Table getTable(String tableName) {
        // Check server for table information
        if (!tableTagsByName.containsKey(tableName)) {
            int i = 0;
            while (tablesByTag.containsKey(i)) {
                i++;
            }
        }
        return null;
    }
}
