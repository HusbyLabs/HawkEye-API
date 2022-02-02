package com.husbylabs.warptables.providers;

import java.util.function.Consumer;

/**
 * @author Noah Husby
 */
public class WebSocketServerProvider extends Provider {
    @Override
    public void send(byte[] data) {

    }

    @Override
    public ProviderMetadata getMetadata() {
        return new ProviderMetadata(true, false, true);
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public String getName() {
        return "Websocket Server";
    }
}
