package com.husbylabs.warptables.providers;

import com.husbylabs.warptables.WarpTable;
import lombok.Getter;

import java.util.function.Consumer;

public abstract class Provider {
    @Getter
    private WarpTable instance;

    public abstract void send(byte[] data);

    public void init(WarpTable instance) {
        this.instance = instance;
    }
}
