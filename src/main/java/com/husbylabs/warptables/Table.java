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
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public class Table {
    protected final Map<String, Field> fields = new ConcurrentHashMap<>();
    @Getter
    private final WarpTableInstance instance;
    @Getter
    private final String path;

    /**
     * Gets a sub-table with the path of the current table as root
     *
     * @param path Path of the sub-table
     * @return The sub-table
     */
    public Table getTable(String path) {
        return instance.getTable(this.path + "/" + path);
    }

    /**
     * Checks if the field exists in the table\
     *
     * @param name Name of field
     * @return True if exists, false if not
     */
    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    /**
     * Get a {@link Field} by its name.
     * A new field will be generated if one by its name does not exist.
     *
     * @param name Name of field
     * @return {@link Field}
     */
    public Field getField(String name) {
        if (hasField(name)) {
            return fields.get(name);
        }
        return instance.getField(getPath() + "/" + name);
    }

    /**
     * Get a {@link Field} by its id.
     *
     * @param id Id of field
     * @return {@link Field}
     */
    public Field getField(int id) {
        return fields.get(id);
    }
}
