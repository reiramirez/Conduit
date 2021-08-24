package com.hayachikin.conduit.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public final class FieldUtils {
    // recursive field searching
    public static ArrayList<Field> getAllFields(Class<?> objectClass) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(objectClass.getDeclaredFields()));
        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != null) fields.addAll(getAllFields(superClass));

        return fields;
    }
}
