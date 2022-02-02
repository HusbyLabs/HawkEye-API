package com.husbylabs.warptables;

import com.husbylabs.warptables.providers.Provider;

/**
 * @author Noah Husby
 */
public class WarpTableServer extends WarpTable {
    protected WarpTableServer(Provider provider) {
        super(provider);
    }

    @Override
    public void start() {
        WarpTableAPI.getLogger().info("Starting WarpTable server");
        getProvider().init(this);
    }
}
