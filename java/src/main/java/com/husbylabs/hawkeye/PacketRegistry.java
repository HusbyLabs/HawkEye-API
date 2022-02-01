package com.husbylabs.hawkeye;

import com.husbylabs.hawkeye.packets.ClientHandshake;
import com.husbylabs.hawkeye.packets.ServerHandshake;
import com.husbylabs.hawkeye.packets.UpdateField;
import com.husbylabs.hawkeye.packets.UpdateValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry for assigning a packet tag to each packet
 * @author Noah Husby
 */
public class PacketRegistry {

    private static final Map<Byte, Packet> packetsByTag = new HashMap<>();
    private static final Map<Object, Byte> tagByClass = new HashMap<>();

    static {
        register(Packet.CLIENT_HANDSHAKE, ClientHandshake.class);
        register(Packet.SERVER_HANDSHAKE, ServerHandshake.class);
        register(Packet.UPDATE_FIELD, UpdateField.class);
        register(Packet.UPDATE_VALUE, UpdateValue.class);
    }

    /**
     * Encodes outgoing data with packet tag
     *
     * @param clazz The packet class
     * @param data The packet data
     * @return The encoded binary data
     */
    public static byte[] encode(Object clazz, byte[] data) {
        byte[] out = new byte[data.length + 1];
        out[0] = tagByClass.get(clazz);
        System.arraycopy(data, 0, out, 1, data.length);
        return out;
    }

    /**
     * Decodes incoming binary data
     *
     * @param data Incoming binary data
     * @return {@link PacketMetadata}
     */
    public static PacketMetadata decode(byte[] data) {
        return new PacketMetadata(
                Arrays.copyOfRange(data, 1, data.length),
                getPacket(data[0])
        );
    }

    /**
     * Register a packet
     *
     * @param packet {@link Packet}
     * @param clazz The matching protobuf packet
     */
    public static void register(Packet packet, Object clazz) {
        packetsByTag.put(packet.getTag(), packet);
        tagByClass.put(clazz, packet.getTag());
    }

    /**
     * Gets the packet tag from the protobuf packet class
     *
     * @param clazz The protobuf packet class
     * @return The binary tag for the packet
     */
    public static byte getTag(Object clazz) {
        return tagByClass.get(clazz);
    }

    /**
     * Gets the {@link Packet} from the packet tag
     *
     * @param tag The packet tag
     * @return {@link Packet}
     */
    public static Packet getPacket(byte tag) {
        return packetsByTag.get(tag);
    }
}
