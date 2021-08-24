package com.hayachikin.conduit.tests.integration.common.models;

import java.util.Objects;

public class SimpleModelSubclass extends SimpleModel {
    String value2;

    public SimpleModelSubclass() {}
    public SimpleModelSubclass(int id, String value1, String value2) {
        this.id = id;
        this.value = value1;
        this.value2 = value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue2() {
        return value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SimpleModelSubclass that = (SimpleModelSubclass) o;
        return Objects.equals(value2, that.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value2);
    }
}
