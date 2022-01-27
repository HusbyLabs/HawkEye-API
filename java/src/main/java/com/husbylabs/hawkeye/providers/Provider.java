package com.husbylabs.hawkeye.providers;

import java.util.function.Consumer;

public interface Provider {
    void send(byte[] data);

    void handleMessage(Consumer<byte[]> msg);
}
