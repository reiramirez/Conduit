package com.hayachikin.conduit;

public class DatabaseOptions {
    final String host, username, password;
    final int port;
    final boolean temporaryCollections;

    public DatabaseOptions(String host, int port, String username, String password) {
        this(host, port, username, password, false);
    }
    public DatabaseOptions(String host, int port, String username, String password, boolean temporaryCollections) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.temporaryCollections = temporaryCollections;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public boolean isTemporaryCollections() {
        return temporaryCollections;
    }
}
