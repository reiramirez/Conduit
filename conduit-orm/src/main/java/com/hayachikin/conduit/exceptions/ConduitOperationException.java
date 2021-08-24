package com.hayachikin.conduit.exceptions;

import com.hayachikin.conduit.Database;

/**
 * An exception thrown when a Conduit database encounters an error.
 */
public class ConduitOperationException extends ConduitException {
    public ConduitOperationException(Database database, String message) {
        super("Database: " + database.getName() + "\nError: " + message);
    }
}
