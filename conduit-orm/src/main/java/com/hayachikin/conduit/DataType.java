package com.hayachikin.conduit;

import com.hayachikin.conduit.annotations.Flattened;

public enum DataType {
    // region Scalar types
    // Integer types
    LONG, INTEGER, SHORT, BYTE,

    // Floating-point types
    DOUBLE, FLOAT,

    // Textual
    STRING,

    // Boolean
    BOOLEAN,

    // Special types
    ENUM,
    UUID,
    // endregion

    // Flattened (for special uses only)
    FLATTENED,

    // Unhandled
    UNHANDLED,

    ;

    public static DataType forClass(Class<?> typeClass) {
        if (typeClass.isAnnotationPresent(Flattened.class))
            return FLATTENED;

        if (typeClass.isEnum())
            return ENUM;
        if (typeClass.equals(Long.class) || typeClass.equals(long.class))
            return LONG;
        if (typeClass.equals(Integer.class) || typeClass.equals(int.class))
            return INTEGER;
        if (typeClass.equals(Short.class) || typeClass.equals(short.class))
            return SHORT;
        if (typeClass.equals(Byte.class) || typeClass.equals(byte.class))
            return BYTE;
        if (typeClass.equals(Double.class) || typeClass.equals(double.class))
            return DOUBLE;
        if (typeClass.equals(Float.class) || typeClass.equals(float.class))
            return FLOAT;
        if (typeClass.equals(String.class))
            return STRING;
        if (typeClass.equals(Boolean.class) || typeClass.equals(boolean.class))
            return BOOLEAN;
        if (typeClass.equals(java.util.UUID.class))
            return UUID;

        return UNHANDLED;
    }
}
