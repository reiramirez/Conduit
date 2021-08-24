package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.Objects;

public class SimpleModel {
    @Id
    protected int id;
    protected String value;

    public SimpleModel() {}
    public SimpleModel(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public void setValue(String value) { this.value = value; }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleModel that = (SimpleModel) o;
        return id == that.id &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
