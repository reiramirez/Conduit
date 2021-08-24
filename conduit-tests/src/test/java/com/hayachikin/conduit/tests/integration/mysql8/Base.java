package com.hayachikin.conduit.tests.integration.mysql8;

import com.hayachikin.conduit.Conduit;
import com.hayachikin.conduit.Database;
import com.hayachikin.conduit.DatabaseOptions;
import com.hayachikin.conduit.DatabaseType;
import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.tests.integration.ConduitTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

abstract class Base extends ConduitTests {
    @Override
    protected Database newDatabase() throws ConduitException {
        DatabaseOptions dbOptions = new DatabaseOptions("localhost", 3306, "root", "password", true);
        return Conduit.createDatabase(DatabaseType.MySQL8, "conduitTests", dbOptions);
    }
}
