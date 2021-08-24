package com.hayachikin.conduit.exceptions;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * An exception thrown when a given class cannot be mapped to a collection.
 */
public class ConduitModelException extends ConduitException {
    public ConduitModelException(String message, Type type) {
        super(String.join(" ", message,
                type instanceof Class<?> objectClass ?
                        objectClass.getCanonicalName() :
                        type.toString()));
    }

    public ConduitModelException(String message, Field field) {
        super(String.join(" ", message, field.getDeclaringClass().getCanonicalName() + "." + field.getName()));
    }
}
