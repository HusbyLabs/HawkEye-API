package com.husbylabs.hawkeye;

import com.husbylabs.hawkeye.providers.Provider;
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
public class HawkEyeAPI {
    @Getter
    @Setter
    private static Logger logger = new SimpleLogger("[HawkEye]", Level.INFO, true, false, true, false, "[yyyy/MM/dd HH:mm:ss]", null, PropertiesUtil.getProperties(), System.out);
    /**
     * Creates a new HawkEye client
     *
     * @param provider The data {@link Provider}
     * @return A new {@link HawkEye} client
     */
    public static HawkEye createClient(@NonNull Provider provider) {
        return new HawkEye(provider, false);
    }

    /**
     * Creates a new HawkEye server
     *
     * @param provider The data {@link Provider}
     * @return A new {@link HawkEye} client
     */
    public static HawkEye createServer(@NonNull Provider provider) {
        return new HawkEye(provider, true);
    }
}
