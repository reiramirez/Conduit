package com.hayachikin.conduit;

import com.hayachikin.conduit.annotations.*;
import com.hayachikin.conduit.exceptions.ConduitMappingException;
import com.hayachikin.conduit.exceptions.ConduitModelException;
import com.hayachikin.conduit.mapping.Mapping;
import com.hayachikin.conduit.schema.Attribute;
import com.hayachikin.conduit.schema.AttributeMap;
import com.hayachikin.conduit.schema.FlattenedAttribute;
import com.hayachikin.conduit.translation.ValueTransformer;
import com.hayachikin.conduit.utils.FieldUtils;
import com.hayachikin.conduit.utils.TypeUtils;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.*;
import java.util.*;

public abstract class ObjectMapper {
    private static final Objenesis objenesis = new ObjenesisStd();

    // Collection mappers
    private final Hashtable<Class<?>, Mapping<?>> mappings = new Hashtable<>();
    private final Hashtable<Class<?>, ValueTransformer<?, ?>> valueTransformers = new Hashtable<>();

    @SuppressWarnings("unchecked")
    protected <T> Mapping<T> mapClass(Class<T> classToMap) throws ConduitModelException {
        Mapping<T> mapping = (Mapping<T>) mappings.get(classToMap);

        if (mapping != null)
            return mapping;
        else {
            if (classToMap.isAnnotationPresent(MappedBy.class)) {
                MappedBy mappedBy = classToMap.getAnnotation(MappedBy.class);
                Class<?> uncastedClass = mappedBy.mapper();
                try {
                    Class<? extends Mapping<T>> mappingClass = (Class<? extends Mapping<T>>) uncastedClass;
                    mapping = mappingClass.getConstructor().newInstance();
                } catch (InvocationTargetException e) {
                    throw new ConduitModelException("Error encountered while constructing custom mapper class", uncastedClass);
                } catch (InstantiationException | NoSuchMethodException e) {
                    throw new ConduitModelException("No no-args constructor found for custom mapper class", uncastedClass);
                } catch (IllegalAccessException e) {
                    throw new ConduitModelException("No no-args constructor accessible for custom mapper class", uncastedClass);
                } catch (ClassCastException e) {
                    throw new ConduitModelException("Invalid mapper class type: ", uncastedClass);
                }
            } else mapping = new Mapping.Base<>(objenesis.getInstantiatorOf(classToMap));

            mappings.put(classToMap, mapping);
        }

        HashSet<String> storedKeys = new HashSet<>();
        for (Field field : FieldUtils.getAllFields(classToMap)) {
            // ignore transient and static fields
            int mod = field.getModifiers();
            if (Modifier.isTransient(mod) || Modifier.isStatic(mod))
                continue;

            String key;
            if (field.isAnnotationPresent(Column.class)) {
                Column annotation = field.getAnnotation(Column.class);
                key = annotation.key();
            } else key = field.getName();

            if (storedKeys.contains(key))
                throw new ConduitModelException("Duplicate key \"" + key + "\" found for class", classToMap);
            else
                storedKeys.add(key);

            Class<?> fieldClass = field.getType();
            Type fieldType = field.getGenericType();
//            if (field.isAnnotationPresent(External.class)) {
//                if (mappings.containsKey(fieldClass)) {
//
//                }
//                else
//                    externalCollection = createCollection(field, collectionName, primaryAttribute);
//            }
            if (field.isAnnotationPresent(Flattened.class)) {
                if (field.isAnnotationPresent(Id.class))
                    throw new ConduitModelException("Id field is not flattenable:", fieldType);

                if (!TypeUtils.isScalar(fieldType))
                    throw new ConduitModelException("Non-scalar field is not flattenable:", fieldType);

                Mapping<?> internalMapping = mapClass(fieldClass);
                mapping.addFlattenedField(field, key, internalMapping);
            } else {
                DataType dataType = DataType.forClass(fieldClass);

                if (dataType == DataType.UNHANDLED)
                    throw new ConduitModelException("UNHANDLED DATA TYPE", fieldClass);
                else
                    mapping.addField(field, key, getValueTransformer(fieldClass));
            }
        }

        return mapping;
    }
    final AttributeMap getAttributeMap(Mapping<?> mapping) throws ConduitModelException {
        List<Attribute> attributes = new ArrayList<>(), primaryAttributes = new ArrayList<>();
        for (Map.Entry<String, Field> e2 : mapping.getFieldMappings().entrySet()) {
            String key = e2.getKey();
            Field field = e2.getValue();
            Class<?> fieldClass = field.getType();

            Attribute attribute;
            if (field.isAnnotationPresent(Flattened.class)) {
                Mapping<?> internalMapping = mapping.getFlattenedMapping(key);
                if (internalMapping == null)
                    throw new ConduitModelException("Flattened field does not have mapping", field);

                attribute = new FlattenedAttribute(key, getAttributeMap(internalMapping).attributes());
            }
            else {
                DataType dataType = DataType.forClass(fieldClass);
                attribute = new Attribute(key, dataType);

                if (field.isAnnotationPresent(Id.class))
                    primaryAttributes.add(attribute);
            }

            attributes.add(attribute);
        }

        return new AttributeMap(attributes, primaryAttributes);
    }
    protected <I, O> void addValueTransformer(Class<I> valueClass, ValueTransformer<I, O> valueTransformer) {
        valueTransformers.put(valueClass, valueTransformer);
    }
    protected <I> ValueTransformer<?, ?> getValueTransformer(Class<I> valueClass) {
        return valueTransformers.get(valueClass);
    }
    /**
     * Maps a {@code HashMap} of {@code Attribute}s and {@code Object}s to an object of type {@code T}.
     *
     * @param objectClass
     * A {@code Class} of type {@code T}
     *
     * @param dataMap
     * A {@code HashMap} of {@code String}s and {@code Object}s containing the values to be stored in the instance
     *
     * @return an instance of type {@code T}
     */
    @SuppressWarnings("unchecked")
    <T> T mapToInstance(Class<T> objectClass, HashMap<String, Object> dataMap) throws ConduitMappingException {
        Mapping<T> mapping = (Mapping<T>) mappings.get(objectClass);
        return mapping.read(dataMap);
//        ClassMapping<T> cm = (ClassMapping<T>) classMappings.get(objectClassName);
//
//        T object = cm.instanceConstructor.newInstance(objectMap);
//        for(Map.Entry<String, Object> entry : objectMap.entrySet()) {
//            Field f = cm.keyFieldMap.get(entry.getKey());
//
//            if (f == null)
//                continue;
//
//            if (!f.isAccessible())
//                f.setAccessible(true);
//            try {
//                f.set(object, db.getTypeTranslator().convertToClassValue(f.getType(), entry.getValue()));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return object;
    }

    /**
     * Maps an instance of {@code T} to a {@code HashMap} of {@code String}s and {@code Object}s.
     *
     * @param object
     * The instance to map
     *
     * @return a {@code HashMap} of {@code String}s and {@code Object}s representing the instance
     */
    @SuppressWarnings("unchecked")
    <T> HashMap<String, Object> mapToDataMap(T object) throws ConduitMappingException {
        Mapping<T> mapping = (Mapping<T>) mappings.get(object.getClass());
        return mapping.write(object);
    }

    @SuppressWarnings("unchecked")
    Object translateToDBValue(Object classValue) throws ConduitMappingException {
        ValueTransformer<Object, Object> vt = (ValueTransformer<Object, Object>) valueTransformers.get(classValue.getClass());
        if (vt == null)
            return classValue;
        else
            return vt.write(classValue);
    }

    @SuppressWarnings("unchecked")
    Object translateToClassValue(ValueTransformer<?, ?> valueTransformer, Object dbValue) throws ConduitMappingException {
        ValueTransformer<Object, Object> vt = (ValueTransformer<Object, Object>) valueTransformer;
        if (valueTransformer == null)
            return dbValue;
        else
            return vt.read(dbValue);
    }
}
