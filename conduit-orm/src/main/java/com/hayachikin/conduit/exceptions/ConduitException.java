package com.hayachikin.conduit.exceptions;

public abstract class ConduitException extends Exception {
    protected ConduitException(String message) {
        super(message);
    }
}
