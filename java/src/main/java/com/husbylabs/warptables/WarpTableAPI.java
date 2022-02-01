package com.husbylabs.warptables;

import com.husbylabs.warptables.providers.Provider;
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
    private static Logger logger = new SimpleLogger("[HawkEye]", Level.INFO, true, false, true, false, "[yyyy/MM/dd HH:mm:ss]", null, PropertiesUtil.getProperties(), System.out);
    /**
     * Creates a new WarpTable client
     *
     * @param provider The data {@link Provider}
     * @return A new {@link WarpTable} client
     */
    public static WarpTable createClient(@NonNull Provider provider) {
        return new WarpTable(provider, false);
    }

    /**
     * Creates a new WarpTable server
     *
     * @param provider The data {@link Provider}
     * @return A new {@link WarpTable} client
     */
    public static WarpTable createServer(@NonNull Provider provider) {
        return new WarpTable(provider, true);
    }
}
