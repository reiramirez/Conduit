package com.hayachikin.conduit.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class TypeUtils {
    private static final Map<Type, Class<?>> WRAPPER_TYPE_MAP;
    static {
        WRAPPER_TYPE_MAP = new HashMap<>(16);
        WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        WRAPPER_TYPE_MAP.put(Character.class, char.class);
        WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TYPE_MAP.put(Double.class, double.class);
        WRAPPER_TYPE_MAP.put(Float.class, float.class);
        WRAPPER_TYPE_MAP.put(Long.class, long.class);
        WRAPPER_TYPE_MAP.put(Short.class, short.class);
        WRAPPER_TYPE_MAP.put(Void.class, void.class);
    }

    public static boolean isPrimitiveOrWrapper(Type type) {
        return WRAPPER_TYPE_MAP.containsKey(type);
    }

    public static boolean isScalar(Type type) {
        if (type instanceof ParameterizedType pType) {
            for (Type argType : pType.getActualTypeArguments())
                if (!isScalar(argType))
                    return false;
        }

        if (type instanceof Class<?> typeClass)
            return !typeClass.isArray() && !java.util.Collection.class.isAssignableFrom(typeClass) && !java.util.Map.class.isAssignableFrom(typeClass);

        else
            return false;
    }

    public static boolean isNotFlattenable(Type type) {
        if (type instanceof ParameterizedType pType) {
            for (Type argType : pType.getActualTypeArguments())
                if (isNotFlattenable(argType))
                    return true;
        }

        return false;
    }
}
