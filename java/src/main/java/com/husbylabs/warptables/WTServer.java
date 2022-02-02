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

import com.google.protobuf.InvalidProtocolBufferException;
import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.providers.Provider;

import java.util.Arrays;

/**
 * @author Noah Husby
 */
public class WTServer extends WTClient {
    protected WTServer(Provider provider) {
        super(provider);
    }

    @Override
    public void start() {
        WarpTablesAPI.getLogger().info("Starting WarpTable server");
    }

    @Override
    public void onMessage(byte[] data) {
        PacketMetadata metadata = PacketRegistry.decode(data);
        switch (metadata.getPacket()) {
            case CLIENT_HANDSHAKE:
                handleClientHandshake(data);
                return;
        }
        super.onMessage(data);
    }

    private void handleClientHandshake(byte[] data) {
        try {
            ClientHandshake clientHandshake = ClientHandshake.parseFrom(data);
            boolean compatible = Arrays.asList(Constants.COMPATIBLE_PROTOCOL_VERSIONS).contains(clientHandshake.getProtocol());
            ServerHandshake handshake = ServerHandshake.newBuilder()
                    .setProtocol(Constants.PROTO_VER)
                    .setCompatible(compatible)
                    .build();
            provider.send(PacketRegistry.encode(ServerHandshake.class, handshake.toByteArray()));
        } catch (InvalidProtocolBufferException ignored) {
        }
    }
}
