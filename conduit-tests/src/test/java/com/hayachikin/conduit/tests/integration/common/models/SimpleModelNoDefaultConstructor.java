package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.Objects;

public class SimpleModelNoDefaultConstructor {
    @Id
    private int id;

    public SimpleModelNoDefaultConstructor(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleModelNoDefaultConstructor that = (SimpleModelNoDefaultConstructor) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public int getId() { return this.id; }
}
