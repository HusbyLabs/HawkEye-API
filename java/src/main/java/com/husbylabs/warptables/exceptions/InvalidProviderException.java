package com.husbylabs.warptables.exceptions;

/**
 * @author Noah Husby
 */
public class InvalidProviderException extends Exception {
    public InvalidProviderException(boolean server) {
        super("The provider does not support being used as a " + (server ? "server" : "client") + ".");
    }
}
