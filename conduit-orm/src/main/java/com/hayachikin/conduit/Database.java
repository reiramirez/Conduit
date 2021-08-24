package com.hayachikin.conduit;

import com.hayachikin.conduit.annotations.Flattened;
import com.hayachikin.conduit.annotations.Id;
import com.hayachikin.conduit.db.QueryAgent;
import com.hayachikin.conduit.exceptions.*;
import com.hayachikin.conduit.mapping.Mapping;
import com.hayachikin.conduit.schema.Attribute;
import com.hayachikin.conduit.schema.Collection;
import com.hayachikin.conduit.schema.FlattenedAttribute;

import java.lang.reflect.Field;
import java.util.*;

public final class Database {
    private final String name;
    private final DatabaseOptions databaseOptions;
    private final QueryAgent queryAgent;
    private final ObjectMapper objectMapper;
    private boolean initialized = false;

    private final HashMap<Class<?>, Collection> collections;

    Database(String name, DatabaseOptions databaseOptions,
             QueryAgent queryAgent, ObjectMapper objectMapper) {
        this.name = name;
        this.databaseOptions = databaseOptions;

        this.queryAgent = queryAgent;
        this.objectMapper = objectMapper;

        this.collections = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    // region Class mapping
    /**
     * <p>Maps a class to a collection in the database.</p>
     *
     * @param objectClass
     * the class to map
     *
     * @throws ConduitOperationException
     * if this method tries to map a class while the database is already initialized
     */
    public void mapClass(Class<?> objectClass) throws ConduitOperationException {
        if (this.collections.containsKey(objectClass)) return;

        if (initialized)
            throw new ConduitOperationException(this, "Database cannot map a new class while initialized");

        Collection collection = new Collection(objectClass);
        this.collections.put(objectClass, collection);
    }

    /**
     * Initializes the database, forming all schema and connections. A database must be initialized before queries can
     * be made.
     */
    public void initialize() throws ConduitModelException, ConduitQueryAgentException {
        if (initialized) return;

        for (Map.Entry<Class<?>, Collection> e1 : collections.entrySet()) {
            Class<?> collectionClass = e1.getKey();
            Collection collection = e1.getValue();
            Mapping<?> mapping = objectMapper.mapClass(collectionClass);
            collection.setAttributeMap(objectMapper.getAttributeMap(mapping));
        }

        for (Collection collection : collections.values())
            this.queryAgent.createCollection(collection);

        initialized = true;
    }
    // endregion
    // region Select queries
    /**
     * <p>Looks for the first entry in the collection mapped to the given class where the value of the entry's primary attribute
     * (i.e. the field in the given class annotated with {@link Id}) is equal to the given value, and returns it as an
     * instance of the given class, or {@code null} if no qualified entry is found.</p>
     * <p>This method should only be used when the given class only has one primary key. Retrieving entries from classes
     * with multiple primary keys using this method may result in inconsistent behavior. For those use cases, use the method
     * {@link #select(Class, HashMap)}</p>
     * <p>Note: this is equivalent to the {@code SELECT} query for SQL databases, and their NoSQL counterparts.</p>
     *
     * @param objectClass
     * the class of the collection to find the entry in
     *
     * @param id
     * the value of the primary key used to filter the database results
     *
     * @param <T>
     * the type of the given class, and the object to return
     *
     * @return an object of type {@code T}, or null if no qualified entry is found
     *
     * @throws ConduitMappingException
     * if the given value for {@code id} is of a type that cannot be mapped by the database
     *
     * @throws ConduitQueryAgentException
     * if data cannot be retrieved due to a database-specific error
     */
    public <T> T select(Class<T> objectClass, Object id) throws ConduitOperationException, ConduitMappingException, ConduitQueryAgentException {
        Collection collection = this.collections.get(objectClass);

        Attribute primaryAttribute = collection.getPrimaryAttributes().get(0);
        HashMap<String, Object> filter = new HashMap<>();
        filter.put(primaryAttribute.getKey(), id);
        return selectInternal(objectClass, collection, filter).stream().findFirst().orElse(null);
    }

    /**
     * <p>Looks for the first entry in the collection mapped to the given class that matches the given filter,
     * and returns it as an instance of the given class, or {@code null} if no qualified entry is found.</p>
     * <p>Note: this is equivalent to the {@code SELECT} query for SQL databases, and their NoSQL counterparts.</p>
     *
     * @param objectClass
     * the class of the collection to find the entry in
     *
     * @param filter
     * a {@code HashMap} of keys and values used to filter the results
     *
     * @param <T>
     * the type of the given class, and the object to return
     *
     * @return an object of type {@code T}, or null if no qualified entry is found
     *
     * @throws ConduitMappingException
     * if the given value for {@code id} is of a type that cannot be mapped by the database
     *
     * @throws ConduitQueryAgentException
     * if data cannot be retrieved due to a database-specific error
     */
    public <T> T select(Class<T> objectClass, HashMap<String, Object> filter) throws ConduitOperationException, ConduitMappingException, ConduitQueryAgentException {
        Collection collection = this.collections.get(objectClass);
        return selectInternal(objectClass, collection, filter).stream().findFirst().orElse(null);
    }

    /**
     * <p>Retrieves all entries in the collection mapped to the given class, and returns them as instances of the given
     * class.</p>
     * <p>Note: this is equivalent to the {@code SELECT} query for SQL databases, and their NoSQL counterparts.</p>
     *
     * @param objectClass
     * the class of the collection to find the entry in
     *
     * @param <T>
     * the type of the given class, and the object to return
     *
     * @return a list of objects of type {@code T}, which may be empty
     *
     * @throws ConduitQueryAgentException
     * if an entry's data received from the database cannot be mapped to an instance of type {@code T}
     */
    public <T> List<T> selectAll(Class<T> objectClass) throws ConduitOperationException, ConduitMappingException, ConduitQueryAgentException {
        Collection collection = this.collections.get(objectClass);
        return selectInternal(objectClass, collection, null);
    }

    private <T> List<T> selectInternal(Class<T> objectClass, Collection collection, HashMap<String, Object> filter) throws ConduitOperationException, ConduitMappingException, ConduitQueryAgentException {
        if (!initialized)
            throw new ConduitOperationException(this, "Select query was called on a database before initializing");

        for (Map.Entry<String, Object> entry : filter.entrySet())
            entry.setValue(objectMapper.translateToDBValue(entry.getValue()));

        List<HashMap<String, Object>> dataMaps = queryAgent.select(collection, filter);

        List<T> objects = new ArrayList<>();
        for (HashMap<String, Object> dataMap : dataMaps)
            objects.add(objectMapper.mapToInstance(objectClass, dataMap));
        return objects;
    }
    // endregion
    // region Insert query
    @SafeVarargs
    public final <T> void insert(Class<T> objectClass, T... objects) throws ConduitOperationException, ConduitQueryAgentException, ConduitMappingException {
        if (!initialized)
            throw new ConduitOperationException(this, "Insert query was called on a database before initializing");

        Collection collection = this.collections.get(objectClass);

        List<HashMap<String, Object>> dataMaps = new ArrayList<>();
        for (T object : objects)
            dataMaps.add(objectMapper.mapToDataMap(object));

        this.queryAgent.insert(collection, dataMaps);
    }
    // endregion
    // region Update query
    @SafeVarargs
    public final <T> void update(Class<T> objectClass, T... objects) throws ConduitOperationException, ConduitQueryAgentException, ConduitMappingException {
        if (!initialized)
            throw new ConduitOperationException(this, "Update query was called on a database before initializing");

        Collection collection = this.collections.get(objectClass);

        List<HashMap<String, Object>> dataMaps = new ArrayList<>();
        for (T object : objects)
            dataMaps.add(objectMapper.mapToDataMap(object));

        this.queryAgent.update(collection, dataMaps);
    }
    // endregion
    // region Upsert query
    @SafeVarargs
    public final <T> void upsert(Class<T> objectClass, T... objects) throws ConduitOperationException, ConduitQueryAgentException, ConduitMappingException {
        if (!initialized)
            throw new ConduitOperationException(this, "Upsert query was called on a database before initializing");

        Collection collection = this.collections.get(objectClass);

        List<HashMap<String, Object>> dataMaps = new ArrayList<>();
        for (T object : objects)
            dataMaps.add(objectMapper.mapToDataMap(object));

        this.queryAgent.upsert(collection, dataMaps);
    }
    // endregion
    // region Delete queries
    public <T> void delete(Class<T> objectClass, Object id) throws ConduitOperationException, ConduitMappingException, ConduitQueryAgentException {
        Collection collection = this.collections.get(objectClass);

        Attribute primaryAttribute = collection.getPrimaryAttributes().get(0);
        HashMap<String, Object> filter = new HashMap<>();
        filter.put(primaryAttribute.getKey(), id);
        deleteInternal(collection, filter);
    }

    private void deleteInternal(Collection collection, HashMap<String, Object> filter) throws ConduitOperationException, ConduitQueryAgentException, ConduitMappingException {
        if (!initialized)
            throw new ConduitOperationException(this, "Delete query was called on a database before initializing");

        for (Map.Entry<String, Object> entry : filter.entrySet())
            entry.setValue(objectMapper.translateToDBValue(entry.getValue()));

        queryAgent.delete(collection, filter);
    }
    // endregion
}
