package com.husbylabs.warptables;

import com.husbylabs.warptables.packets.ClientHandshake;
import com.husbylabs.warptables.packets.UpdateFieldOuterClass;
import com.husbylabs.warptables.providers.Provider;
import com.husbylabs.warptables.util.PacketUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The HawkEye client for writing and reading data
 *
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class WarpTable {
    @Getter
    private final Provider provider;
    @Getter
    private final boolean server;

    private Map<Integer, WarpTableEntry> fields = new HashMap<>();
    private Map<String, Integer> fieldTags = new HashMap<>();

    private State state = State.HANDSHAKE;

    /**
     * Start the WarpTable connection
     */
    public void start() {
        WarpTableAPI.getLogger().info("Starting WarpTable client");
        provider.handleMessage(this::handleMessage);
        if (!server) {
            // Send handshake to server
            ClientHandshake handshake = ClientHandshake.newBuilder()
                    .setProtocol(Constants.PROTO_VER)
                    .build();
            provider.send(PacketUtil.identify(ClientHandshake.class, handshake.toByteArray()));
        }
    }

    public void setFieldInfo(FieldInfo info) {

    }

    public void setFieldsInfo(Collection<FieldInfo> info) {

    }

    /**
     * Gets a {@link WarpTableEntry} by its registration id
     *
     * @param id The registration id
     * @return {@link WarpTableEntry} if exists, null if not
     */
    public WarpTableEntry get(int id) {
        return fields.get(id);
    }

    /**
     * Gets a {@link WarpTableEntry} by its tag
     * @param tag The tag
     * @return {@link WarpTableEntry} if exists, otherwise a new entry will be created
     */
    public WarpTableEntry get(@NonNull String tag) {
        if(!fieldTags.containsKey(tag)) {
            WarpTableEntry entry = create();
            fieldTags.put(tag, entry.getId());
            return entry;
        }
        return get(fieldTags.get(tag));
    }

    public WarpTableEntry create() {
        int id = fields.size();
        while(fields.containsKey(id)) {
            id++;
        }
        WarpTableEntry entry = new WarpTableEntry(this, id);
        fields.put(id, entry);
        return entry;
    }

    private void handleMessage(byte[] data) {
        /*
        try {
            ClientHandshake handshake = ClientHandshake.parseFrom(data);
            System.out.println(isServer() + ": " + handshake.getProtocol());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

         */
    }
}
