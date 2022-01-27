package com.husbylabs.hawkeye;

import lombok.RequiredArgsConstructor;

/**
 * @author Noah Husby
 */
@RequiredArgsConstructor
public final class HawkEyeEntry {

    public boolean isValid() {
        return false;
    }

    public int getId() {
        return id;
    }

    public HawkEye getInstance() {
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


    private final HawkEye instance;
    private final int id;
}
