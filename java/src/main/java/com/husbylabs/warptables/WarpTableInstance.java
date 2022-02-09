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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class WarpTableInstance {
    @Getter
    protected final InetSocketAddress address;

    protected final Map<Integer, Table> tablesByTag = new HashMap<>();
    protected final Map<String, Integer> tableTagsByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public abstract void start() throws Exception;

    public abstract void stop();

    public abstract Table getTable(String table);
}
