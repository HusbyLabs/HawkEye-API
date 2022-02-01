package com.husbylabs.hawkeye;

import com.husbylabs.hawkeye.packets.UpdateField;
import lombok.Data;

/**
 * A class representing the identify of a field
 * @author Noah Husby
 */
@Data
public class FieldInfo {
    private final String tag;
    private final int id;
    private final EntryType type;

    public UpdateField toProtobuf() {
        return UpdateField.newBuilder()
                .setName(tag)
                .setId(id)
                .build();
    }
}
