package com.husbylabs.warptables;

import lombok.experimental.UtilityClass;

/**
 * @author Noah Husby
 */
@UtilityClass
public final class Constants {

    public static final int PROTO_VER = 100;
    public static final Integer[] COMPATIBLE_PROTOCOL_VERSIONS = {100};

    public static final class Packets {
        public static final byte HEARTBEAT = 0x00;
        public static final byte READY = 0x02;

    }
}
