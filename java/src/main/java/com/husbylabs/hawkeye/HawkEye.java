package com.husbylabs.hawkeye;

import com.google.protobuf.InvalidProtocolBufferException;
import com.husbylabs.hawkeye.packets.ClientHandshake;
import com.husbylabs.hawkeye.packets.UpdateFieldOuterClass;
import com.husbylabs.hawkeye.providers.Provider;
import com.husbylabs.hawkeye.util.PacketUtil;
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
public final class HawkEye {
    @Getter
    private final Provider provider;
    @Getter
    private final boolean server;

    private Map<Integer, HawkEyeEntry> fields = new HashMap<>();
    private Map<String, Integer> fieldTags = new HashMap<>();

    private State state = State.HANDSHAKE;

    /**
     * Start the HawkEye connection
     */
    public void start() {
        HawkEyeAPI.getLogger().info("Starting HawkEye client");
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
     * Gets a {@link HawkEyeEntry} by its registration id
     *
     * @param id The registration id
     * @return {@link HawkEyeEntry} if exists, null if not
     */
    public HawkEyeEntry get(int id) {
        return fields.get(id);
    }

    /**
     * Gets a {@link HawkEyeEntry} by its tag
     * @param tag The tag
     * @return {@link HawkEyeEntry} if exists, otherwise a new entry will be created
     */
    public HawkEyeEntry get(@NonNull String tag) {
        if(!fieldTags.containsKey(tag)) {
            HawkEyeEntry entry = create();
            fieldTags.put(tag, entry.getId());
            return entry;
        }
        return get(fieldTags.get(tag));
    }

    public HawkEyeEntry create() {
        int id = fields.size();
        while(fields.containsKey(id)) {
            id++;
        }
        HawkEyeEntry entry = new HawkEyeEntry(this, id);
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
