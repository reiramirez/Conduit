package com.hayachikin.conduit.db;

import com.hayachikin.conduit.DatabaseOptions;
import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.exceptions.ConduitQueryAgentException;
import com.hayachikin.conduit.schema.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.List;

public abstract class QueryAgent {
    protected final String name;
    protected final DatabaseOptions dbOptions;

    protected static final Logger logger = LogManager.getLogger(QueryAgent.class);

    protected QueryAgent(String databaseName, DatabaseOptions dbOptions) throws ConduitException {
        this.name = databaseName;
        this.dbOptions = dbOptions;
    }

    // region Database schema methods
    public abstract void createCollection(Collection collection) throws ConduitQueryAgentException;
    public abstract boolean collectionExists(Collection collection);
    public abstract void deleteCollection(Collection collection);
    // endregion

    // region CRUD methods
    public abstract List<HashMap<String, Object>> select(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException;
    public abstract void insert(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException;
    public abstract void update(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException;
    public abstract void upsert(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException;
    public abstract void delete(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException;
    // endregion
}
