package com.husbylabs.hawkeye.providers;

import com.husbylabs.hawkeye.HawkEye;
import lombok.Getter;

import java.util.function.Consumer;

public abstract class Provider {
    @Getter
    private HawkEye instance;

    public abstract void send(byte[] data);

    public abstract void handleMessage(Consumer<byte[]> msg);

    public void init(HawkEye instance) {
        this.instance = instance;
    }
}
