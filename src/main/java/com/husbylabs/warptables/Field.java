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

import com.husbylabs.warptables.packets.FieldUpdate.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
@Getter
public class Field {

    private final int handle;
    private final String base;
    private final Table table;
    private Type type = Type.STRING;
    private Object value = null;

    private static Type getType(Object object) {
        if (object instanceof Number) {
            return Type.NUMBER;
        } else if (object instanceof Boolean) {
            return Type.BOOL;
        } else if (object instanceof String) {
            return Type.STRING;
        } else if (object instanceof String[]) {
            return Type.STRING_ARRAY;
        } else if (object instanceof Boolean[]) {
            return Type.BOOL_ARRAY;
        } else if (object instanceof Number[]) {
            return Type.NUMBER_ARRAY;
        }
        return Type.UNRECOGNIZED;
    }

    public boolean isValid() {
        return isValid(type);
    }

    public boolean isValid(Type type) {
        return type != Type.UNRECOGNIZED;
    }

    public Object setValue(Object value) {
        Type type = getType(value);
        if (!isValid(type)) {
            //TODO: Hell nah
        }
        this.type = type;
        this.value = value;
        table.getInstance().postField(this);
        return value;
    }

    public Object setValue(Type type, String... values) {
        boolean empty = values.length == 0;
        if (type == Type.STRING) {
            value = empty ? "" : values[0];
        } else if (type == Type.BOOL) {
            value = !empty && Boolean.parseBoolean(values[0]);
        } else if (type == Type.NUMBER) {
            try {
                value = empty ? 0 : NumberFormat.getInstance().parse(values[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (type == Type.STRING_ARRAY) {
            value = values;
        } else if (type == Type.BOOL_ARRAY) {
            if (empty) {
                value = new Boolean[]{};
            } else {
                ArrayList<Boolean> booleans = new ArrayList<>();
                Arrays.stream(values).forEach(s -> booleans.add(Boolean.parseBoolean(s)));
                value = booleans.toArray();
            }
        } else if (type == Type.NUMBER_ARRAY) {
            if (empty) {
                value = new Number[]{};
            } else {
                ArrayList<Number> numbers = new ArrayList<>();
                Arrays.stream(values).forEach(s -> {
                    try {
                        numbers.add(NumberFormat.getInstance().parse(s));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                value = numbers.toArray();
            }
        } else {
            type = Type.UNRECOGNIZED;
        }
        this.type = type;
        return value;
    }

    public String getPath() {
        return table.getPath() + "/" + base;
    }

    public Object getValue() {
        return value;
    }

    public long getLastUpdate() {
        return 0;
    }

    public boolean getBoolean() {
        return getBoolean(false);
    }

    public void setBoolean(boolean value) {
        setValue(value);
    }

    public boolean getBoolean(boolean def) {
        return value instanceof Boolean ? (boolean) value : def;
    }

    public Number getNumber() {
        return getNumber(0);
    }

    public void setNumber(Number value) {
        setValue(value);
    }

    public Number getNumber(Number def) {
        return value instanceof Number ? (Number) value : def;
    }

    public String getString(String def) {
        return value instanceof String ? (String) value : def;
    }

    public void setString(String value) {
        setValue(value);
    }

    public void setBooleanArray(Boolean[] value) {
        setValue(value);
    }

    public void setNumberArray(Number[] value) {
        setValue(value);
    }

    public void setStringArray(String[] value) {
        setValue(value);
    }

    public void delete() {

    }
}
