package com.husbylabs.hawkeye;

import com.husbylabs.hawkeye.providers.Provider;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Noah Husby
 */
@UtilityClass
public class HawkEyeAPI {
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
