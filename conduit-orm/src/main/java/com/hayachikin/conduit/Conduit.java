package com.hayachikin.conduit;

import com.hayachikin.conduit.db.mongodb.MongoObjectMapper;
import com.hayachikin.conduit.db.mongodb.MongoQueryAgent;
import com.hayachikin.conduit.db.mysql8.SqlObjectMapper;
import com.hayachikin.conduit.db.mysql8.SqlQueryAgent;
import com.hayachikin.conduit.exceptions.ConduitException;

public final class Conduit {
    // TODO: track databases

    private Conduit() {}

    public static Database createDatabase(DatabaseType type,
                                          String dbName,
                                          String host,
                                          int port,
                                          String username,
                                          String password) throws ConduitException {
        return createDatabase(type, dbName, new DatabaseOptions(host, port, username, password));
    }

    public static Database createDatabase(DatabaseType type,
                                          String dbName,
                                          DatabaseOptions dbOptions) throws ConduitException {
        switch (type) {
            case MySQL8:
                return new Database(dbName, dbOptions, new SqlQueryAgent(dbName, dbOptions), new SqlObjectMapper());
            case MongoDB:
                return new Database(dbName, dbOptions, new MongoQueryAgent(dbName, dbOptions), new MongoObjectMapper());
            default:
                return null;
        }
    }
}
