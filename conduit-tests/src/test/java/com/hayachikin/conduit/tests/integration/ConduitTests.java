package com.hayachikin.conduit.tests.integration;

import com.hayachikin.conduit.Conduit;
import com.hayachikin.conduit.Database;
import com.hayachikin.conduit.DatabaseOptions;
import com.hayachikin.conduit.DatabaseType;
import com.hayachikin.conduit.exceptions.ConduitException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ConduitTests {
    protected Database db;

    protected abstract Database newDatabase() throws ConduitException;

    @BeforeEach
    void setUp() throws ConduitException {
        db = newDatabase();
    }

    @AfterEach
    void cleanUp() throws ConduitException {
        db = null;
    }
}
