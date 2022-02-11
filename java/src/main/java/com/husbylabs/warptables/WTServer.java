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
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Noah Husby
 */
public class WTServer extends WarpTableInstance {

    private final ServerListener listener;
    private final Server server;
    private final Map<Integer, Integer> clients = Maps.newHashMap();

    protected WTServer(InetSocketAddress address) {
        super(address);
        listener = new ServerListener(this);
        ServerBuilder<?> builder = ServerBuilder.forPort(address.getPort());
        listener.listen(builder);
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
    public Table getTable(String table) {
        return null;
    }

}
