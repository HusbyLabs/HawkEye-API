package com.husbylabs.warptables;

import lombok.Data;

/**
 * An entity representing incoming packet data
 * @author Noah Husby
 */
@Data
public class PacketMetadata {
    private final byte[] data;
    private final Packet packet;
}
