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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.husbylabs.warptables.events.EventListener;
import com.husbylabs.warptables.events.state.StatusChangedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WarpTableInstance {
    @Getter
    protected final InetSocketAddress address;

    protected final Map<Integer, String> fields = new ConcurrentHashMap<>();
    protected final Map<String, Table> tables = new ConcurrentHashMap<>();
    protected final List<EventListener> eventListeners = Lists.newArrayList();
    protected final ConcurrentHashMap<String, Field> fieldsToPublish = new ConcurrentHashMap<>();

    private Thread publishThread = null;

    @Getter
    protected Status status = Status.STOPPED;

    private final ExecutorService publishThreads = Executors.newFixedThreadPool(16);

    private double periodicRate = 10;
    private long lastPublish = System.currentTimeMillis();

    /**
     * Normalizes a child path. Examples
     * normalizePath("//foo/bar//child", true) = /foo/bar/child
     * normalizePath("//foo/bar//child", false) = foo/bar/child
     *
     * @param path         The path to normalize
     * @param leadingSlash Whether a leading slash should be added to the beginning of the path
     * @return The normalized path
     */
    public static String normalizePath(String path, boolean leadingSlash) {
        String normalized = leadingSlash ? "/" + path : path;
        normalized = normalized.replaceAll("/{2,}", "/");
        if (!leadingSlash && normalized.charAt(0) == '/') {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    /**
     * Set the periodic rate of the instance. This is how frequent the data should be sent.
     *
     * @param interval The interval rate in seconds. Range: 0.01 - 1.0
     */
    public void setUpdateRate(double interval) {
        if (interval < 0.01 || interval > 1.0) {
            throw new IllegalArgumentException("The interval must be between 0.01 - 1.0");
        }
        this.periodicRate = interval * 1000;
    }

    /**
     * Normalizes a child path with a leading slash. Examples:
     * normalizePath("foo//bar/child") = /foo/bar/child
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    public static String normalizePath(String path) {
        return normalizePath(path, true);
    }

    /**
     * Gets the base identity of a path. Examples:
     * basePath("/foo/bar/child") = "child"
     *
     * @param path The path
     * @return The base of the path
     */
    public static String basePath(String path) {
        final int slash = path.lastIndexOf("/");
        return slash == -1 ? path : path.substring(slash + 1);
    }

    /**
     * Start the server / client
     * This method will block and throw an exception on the client if the server isn't available.
     * If the client has enableAutoConnect() called, then the client will continuously attempt to connect
     * to the server in the background.
     *
     * @throws IOException if the server / client cannot start.
     */
    public abstract void start() throws IOException;

    /**
     * Stop the server / client
     */
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

    protected void setStatus(Status status) {
        this.status = status;
        eventListeners.forEach(l -> l.onStatusChangedEvent(new StatusChangedEvent(this, status)));
    }

    protected void startPublishThread() {
        if (publishThread != null && publishThread.isAlive()) {
            publishThread.interrupt();
        }
        publishThread = new Thread(() -> {
            while (status != Status.STOPPED) {
                if (lastPublish + periodicRate < System.currentTimeMillis()) {
                    lastPublish = System.currentTimeMillis();
                    HashMap<String, Field> temp;
                    synchronized (fieldsToPublish) {
                        temp = Maps.newHashMap(fieldsToPublish);
                        fieldsToPublish.clear();
                    }
                    for (Field field : temp.values()) {
                        publishThreads.submit(() -> publishQueuedField(field));
                    }
                }
            }
        });
        publishThread.setDaemon(true);
        publishThread.start();
    }

    /**
     * Add {@link EventListener} to instance
     *
     * @param listener {@link EventListener}
     */
    public void addEventListener(EventListener listener) {
        this.eventListeners.add(listener);
    }

    public abstract Field getField(String path);

    public void publishField(Field field) {
        fieldsToPublish.putIfAbsent(field.getPath(), field);
    }

    protected abstract void publishQueuedField(Field field);
}
