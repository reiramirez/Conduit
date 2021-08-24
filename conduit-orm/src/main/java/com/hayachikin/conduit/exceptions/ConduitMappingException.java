package com.hayachikin.conduit.exceptions;

/**
 * An exception called when a given value cannot be mapped to an instance of a class.
 */
public class ConduitMappingException extends ConduitException {
    public ConduitMappingException(String message, Class<?> objectClass) {
        super(String.join(" ", message, objectClass.getCanonicalName()));
    }
}
