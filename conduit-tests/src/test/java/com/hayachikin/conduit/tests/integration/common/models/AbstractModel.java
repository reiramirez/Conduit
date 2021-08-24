package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.Objects;

public class AbstractModel {
    @Id
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractModel that = (AbstractModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
