package com.husbylabs.warptables.util;

import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.ServerHandshake;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noah Husby
 */
@UtilityClass
public class PacketUtil {
    private final Map<Object, Byte> identificationMap = new HashMap<>();

    static {
        identificationMap.put(ClientHandshake.class, (byte) 0x0000);
        identificationMap.put(ServerHandshake.class, (byte) 0x0000);
    }

    public static byte[] identify(Object packet, byte[] data) {
        byte[] out = new byte[data.length + 1];
        out[0] = identificationMap.get(packet);
        System.arraycopy(data, 0, out, 1, data.length);
        return out;
    }
}
