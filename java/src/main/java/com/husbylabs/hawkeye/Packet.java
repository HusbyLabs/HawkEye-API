package com.husbylabs.hawkeye;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public enum Packet {
    CLIENT_HANDSHAKE(0), SERVER_HANDSHAKE(1), UPDATE_FIELD(2), UPDATE_VALUE(3);

    @Getter
    private final byte tag;

    Packet(int tag) {
        this((byte) tag);
    }
}
