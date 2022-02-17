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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WarpTableInstance {
    @Getter
    protected final InetSocketAddress address;

    protected final Map<Integer, String> fields = new ConcurrentHashMap<>();
    protected final Map<String, Table> tables = new ConcurrentHashMap<>();

    public static String normalizePath(String path, boolean leadingSlash) {
        String normalized = leadingSlash ? "/" + path : path;
        normalized = normalized.replaceAll("/{2,}", "/");
        if (!leadingSlash && normalized.charAt(0) == '/') {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    public static String basePath(String path) {
        final int slash = path.lastIndexOf("/");
        return slash == -1 ? path : path.substring(slash + 1);
    }

    public static String normalizePath(String path) {
        return normalizePath(path, true);
    }

    public abstract void start() throws Exception;

    public abstract void stop();

    /**
     * Gets a table by its name. A new table will be created with that name if it doesn't exist.
     *
     * @param path The path of the table
     * @return {@link Table}
     */
    public Table getTable(String path) {
        path = normalizePath(path);
        tables.putIfAbsent(path, new Table(this, path));
        return tables.get(path);
    }

    public abstract Field getField(String path);

    public abstract void postField(Field field);
}
