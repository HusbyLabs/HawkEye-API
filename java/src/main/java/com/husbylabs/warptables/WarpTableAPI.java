/*
 * MIT License
 *
 * Copyright (c) 2022 Husby Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 */

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
