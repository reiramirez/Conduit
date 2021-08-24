package com.hayachikin.conduit.exceptions;

/**
 * An exception called when a database-specific error is encountered by the query agent.
 */
public class ConduitQueryAgentException extends ConduitException {
    public ConduitQueryAgentException(String message) {
        super(message);
    }
}
