package com.husbylabs.warptables;

import com.google.protobuf.InvalidProtocolBufferException;
import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.ServerHandshake;
import com.husbylabs.warptables.providers.Provider;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author Noah Husby
 */
public class WarpTableServer extends WarpTable {
    protected WarpTableServer(Provider provider) {
        super(provider);
    }

    @Override
    public void start() {
        uniqueId = UUID.randomUUID();
        WarpTableAPI.getLogger().info("Starting WarpTable server [ID: " + uniqueId + "]");
        openProvider();
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
