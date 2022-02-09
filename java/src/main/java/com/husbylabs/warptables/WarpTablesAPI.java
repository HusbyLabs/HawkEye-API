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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.net.InetSocketAddress;

/**
 * A utility class for creating WarpTables instances
 *
 * @author Noah Husby
 */
@UtilityClass
public class WarpTablesAPI {
    @Getter
    @Setter
    private static Logger logger = new SimpleLogger("[WarpTables]", Level.INFO, true, false, true, false, "[yyyy/MM/dd HH:mm:ss]", null, PropertiesUtil.getProperties(), System.out);

    /**
     * Creates a new WarpTable client
     *
     * @param address The address of the server
     * @return A new {@link WTClient} client
     */
    public static WarpTableInstance createClient(@NonNull InetSocketAddress address) {
        return new WTClient(address);
    }

    /**
     * Creates a new WarpTable server
     *
     * @param port The port of the server
     * @return A new {@link WTClient} client
     */
    public static WarpTableInstance createServer(int port) {
        return new WTServer(new InetSocketAddress("127.0.0.1", port));
    }
}
