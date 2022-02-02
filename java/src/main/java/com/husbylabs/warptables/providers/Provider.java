package com.husbylabs.warptables.providers;

import com.husbylabs.warptables.WarpTable;
import lombok.Getter;

import java.util.function.Consumer;

public abstract class Provider {
    @Getter
    private WarpTable instance;

    public abstract void send(byte[] data);

    public abstract ProviderMetadata getMetadata();

    public abstract void open();

    public abstract void close();

    public abstract boolean isConnected();

    public abstract String getName();

    public void init(WarpTable instance) {
        this.instance = instance;
    }
}
