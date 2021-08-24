package com.hayachikin.conduit.db.mongodb;

import com.hayachikin.conduit.DatabaseOptions;
import com.hayachikin.conduit.db.QueryAgent;
import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.exceptions.ConduitQueryAgentException;
import com.hayachikin.conduit.schema.Collection;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import java.util.List;

public final class MongoQueryAgent extends QueryAgent {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoQueryAgent(String databaseName, DatabaseOptions dbOptions) throws ConduitException {
        super(databaseName, dbOptions);
        ConnectionString connectionString = new ConnectionString("mongodb+srv://" + dbOptions.getUsername() + ":" +
                dbOptions.getPassword() + "@" + dbOptions.getHost() + "/" + databaseName + "?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("databaseName");
    }

    @Override
    public void createCollection(Collection collection) throws ConduitQueryAgentException {
        database.createCollection(collection.getName());
    }

    @Override
    public boolean collectionExists(Collection collection) {
        for (String name : database.listCollectionNames())
            if (name.equalsIgnoreCase(collection.getName()))
                return true;
        return false;
    }

    @Override
    public void deleteCollection(Collection collection) {
        database.getCollection(collection.getName()).drop();
    }

    @Override
    public List<HashMap<String, Object>> select(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException {
        return null;
    }

    @Override
    public void insert(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException {

    }

    @Override
    public void update(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException {

    }

    @Override
    public void upsert(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException {

    }

    @Override
    public void delete(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException {

    }
}
