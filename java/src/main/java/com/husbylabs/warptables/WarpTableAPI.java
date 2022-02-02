package com.husbylabs.warptables;

import com.husbylabs.warptables.exceptions.InvalidProviderException;
import com.husbylabs.warptables.providers.Provider;
import com.husbylabs.warptables.providers.ProviderMetadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * @author Noah Husby
 */
@UtilityClass
public class WarpTableAPI {
    @Getter
    @Setter
    private static Logger logger = new SimpleLogger("[WarpTables]", Level.INFO, true, false, true, false, "[yyyy/MM/dd HH:mm:ss]", null, PropertiesUtil.getProperties(), System.out);
    /**
     * Creates a new WarpTable client
     *
     * @param provider The data {@link Provider}
     * @return A new {@link WarpTable} client
     */
    public static WarpTable createClient(@NonNull Provider provider) throws InvalidProviderException {
        ProviderMetadata metadata = provider.getMetadata();
        if(!metadata.isClientSupported()) {
            throw new InvalidProviderException(false);
        }
        return new WarpTable(provider);
    }

    /**
     * Creates a new WarpTable server
     *
     * @param provider The data {@link Provider}
     * @return A new {@link WarpTable} client
     */
    public static WarpTable createServer(@NonNull Provider provider) throws InvalidProviderException {
        ProviderMetadata metadata = provider.getMetadata();
        if(!metadata.isServerSupported()) {
            throw new InvalidProviderException(true);
        }
        return new WarpTableServer(provider);
    }
}
