package com.husbylabs.hawkeye;

import com.google.protobuf.InvalidProtocolBufferException;
import com.husbylabs.hawkeye.packets.ClientHandshakeOuterClass;
import com.husbylabs.hawkeye.packets.ClientHandshakeOuterClass.ClientHandshake;
import com.husbylabs.hawkeye.providers.Provider;
import com.husbylabs.hawkeye.util.PacketUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The HawkEye client for writing and reading data
 *
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class HawkEye {
    @Getter
    private final Provider provider;
    @Getter
    private final boolean server;

    private State state = State.HANDSHAKE;

    /**
     * Start the HawkEye connection
     */
    public void start() {
        provider.handleMessage(this::handleMessage);
        if (!server) {
            // Send handshake to server
            ClientHandshake handshake = ClientHandshake.newBuilder()
                    .setProtocol(Constants.PROTO_VER)
                    .build();
            provider.send(PacketUtil.identify(ClientHandshake.class, handshake.toByteArray()));
        }
    }

    private void handleMessage(byte[] data) {
        try {
            ClientHandshake handshake = ClientHandshake.parseFrom(data);
            System.out.println(isServer() + ": " + handshake.getProtocol());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
