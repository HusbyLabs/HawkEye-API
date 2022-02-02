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

import lombok.RequiredArgsConstructor;

/**
 * A wrapper class representing a field
 * @author Noah Husby
 */
@RequiredArgsConstructor
public final class WarpTableEntry {

    public boolean isValid() {
        return false;
    }

    public int getId() {
        return id;
    }

    public WTClient getInstance() {
        return instance;
    }

    public boolean exists() {
        byte b = 127;
        return false;
    }

    public String getName() {
        return "";
    }

    public long getLastUpdate() {
        return 0;
    }

    public boolean getBoolean() {
        return getBoolean(false);
    }

    public boolean getBoolean(boolean def) {
        return false;
    }

    public Number getNumber() {
        return getNumber(0);
    }

    public Number getNumber(Number def) {
        return 0;
    }

    public String getString(String def) {
        return "";
    }

    public static boolean isValidType(Object data) {
        return data instanceof Number
               || data instanceof Boolean
               || data instanceof String;
    }

    public boolean setBoolean(boolean value) {
        return false;
    }

    public boolean setDouble(double value) {
        return false;
    }

    public boolean setNumber(Number value) {
        return false;
    }

    public boolean setString(String value) {
        return false;
    }

    public void delete() {

    }

    private final WTClient instance;
    private final int id;
}
