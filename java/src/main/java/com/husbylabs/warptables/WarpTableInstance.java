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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WarpTableInstance {
    @Getter
    protected final InetSocketAddress address;

    protected final Map<Integer, Table> tables = new HashMap<>();
    protected final Map<String, Integer> tablesByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public abstract void start() throws Exception;

    public abstract void stop();

    /**
     * Gets a table by its name. A new table will be created with that name if it doesn't exist.
     *
     * @param table The name of the table
     * @return {@link Table}
     */
    public abstract Table getTable(String table);

    /**
     * Gets a table by its unique id
     *
     * @param id The unique id of the table
     * @return {@link Table} if exists, null if not
     */
    public Table getTable(int id) {
        return tables.get(id);
    }

    /**
     * Handles a new table assigment
     *
     * @param name Name of the table
     * @param id   Id of the table
     * @return {@link Table}
     */
    protected Table handleTable(String name, int id) {
        Table table = new Table(id, name, this);
        tables.put(id, table);
        tablesByName.put(name, id);
        return tables.get(id);
    }

    public abstract Field getField(int tableId, int fieldId);

    public abstract Field getField(int tableId, String name);

    public abstract void postField(Field field);
}
