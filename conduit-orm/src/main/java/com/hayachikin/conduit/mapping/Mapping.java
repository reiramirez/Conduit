package com.hayachikin.conduit.mapping;

import com.hayachikin.conduit.exceptions.ConduitMappingException;
import com.hayachikin.conduit.exceptions.ConduitModelException;
import com.hayachikin.conduit.translation.ValueTransformer;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

public abstract class Mapping<T> implements ValueTransformer<T, HashMap<String, Object>> {
    protected final ObjectInstantiator<T> instantiator;
    protected final Hashtable<String, Field> fieldMappings = new Hashtable<>();
    protected final Hashtable<String, Set<String>> flattenedKeys = new Hashtable<>();
    protected final Hashtable<String, ValueTransformer<?, ?>> valueTransformers = new Hashtable<>();

    protected Mapping(ObjectInstantiator<T> instantiator) {
        this.instantiator = instantiator;
    }

    public final Hashtable<String, Field> getFieldMappings() { return new Hashtable<>(fieldMappings); }
    public final Mapping<?> getFlattenedMapping(String key) {
        ValueTransformer<?, ?> valueTransformer = valueTransformers.get(key);
        if (valueTransformer instanceof Mapping<?> mapping)
            return mapping;
        else
            return null;
    }

    public final void addField(Field field, String attributeKey, ValueTransformer<?, ?> valueTransformer) throws ConduitModelException {
        if (fieldMappings.containsKey(attributeKey))
            throw new ConduitModelException("Tried to map a field with an existing mapping:", field);

        fieldMappings.put(attributeKey, field);
        if (valueTransformer != null)
            valueTransformers.put(attributeKey, valueTransformer);
    }

    public final void addFlattenedField(Field field, String attributeKey, Mapping<?> mapping) throws ConduitModelException {
        addField(field, attributeKey, mapping);
        flattenedKeys.put(attributeKey, mapping.fieldMappings.keySet());
    }

    @Override
    public abstract T read(HashMap<String, Object> valueMap) throws ConduitMappingException;

    @Override
    public abstract HashMap<String, Object> write(T object) throws ConduitMappingException;

    public static class Base<T> extends Mapping<T> {
        public Base(ObjectInstantiator<T> instantiator) {
            super(instantiator);
        }

        @Override @SuppressWarnings("unchecked")
        public T read(HashMap<String, Object> dataMap) throws ConduitMappingException {
            T object = instantiator.newInstance();
            // Compress flattened data
            HashMap<String, HashMap<String, Object>> compressedData = new HashMap<>();
            dataMap.entrySet().removeIf(entry -> {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.contains(".")) { // flattened
                    String[] keyParts = key.split("\\.");
                    compressedData.computeIfAbsent(keyParts[0], k -> new HashMap<>()).put(keyParts[1], value);
                    return true;
                } else return false;
            });
            dataMap.putAll(compressedData);

            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (fieldMappings.containsKey(key)) {
                    ValueTransformer<Object, Object> valueTransformer = (ValueTransformer<Object, Object>) valueTransformers.get(entry.getKey());
                    if (valueTransformer != null)
                        value = valueTransformer.read(value);

                    Field field = fieldMappings.get(key);
                    field.setAccessible(true);
                    try {
                        field.set(object, value);
                    }
                    catch (IllegalAccessException e) {
                        throw new ConduitMappingException("Cannot access field \"" + field.getName() + "\" of class", field.getDeclaringClass());
                    }
                }
            }

            return object;
        }

        @Override @SuppressWarnings("unchecked")
        public HashMap<String, Object> write(T object) throws ConduitMappingException {
            HashMap<String, Object> dataMap = new HashMap<>();
            for (Map.Entry<String, Field> entry : fieldMappings.entrySet()) {
                Field field = entry.getValue();
                field.setAccessible(true);

                String key = entry.getKey();
                Object value;
                try { value = field.get(object); }
                catch (IllegalAccessException e) {
                    throw new ConduitMappingException("Cannot access field \"" + field.getName() + "\" of class", field.getDeclaringClass());
                }

                ValueTransformer<Object, Object> valueTransformer = (ValueTransformer<Object, Object>) valueTransformers.get(key);
                if (valueTransformer != null)
                    value = valueTransformer.write(value);

                dataMap.put(key, value);
            }

            // Flatten compressed data
            HashMap<String, Object> flattenedData = new HashMap<>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (flattenedKeys.containsKey(key)) {
                    HashMap<String, Object> compressedData = (HashMap<String, Object>) value;
                    for (String internalKey : flattenedKeys.get(key))
                        flattenedData.put(key + "." + internalKey, compressedData.get(internalKey));
                    dataMap.entrySet().remove(entry);
                }
            }
            dataMap.putAll(flattenedData);

            return dataMap;
        }
    }
}
