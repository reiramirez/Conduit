package com.hayachikin.conduit.tests.integration.common.models;

import java.util.Objects;

public class AbstractModelSubclass extends AbstractModel {
    String value1;

    public AbstractModelSubclass() {}

    public AbstractModelSubclass(int id, String value1) {
        this.id = id;
        this.value1 = value1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractModelSubclass that = (AbstractModelSubclass) o;
        return Objects.equals(value1, that.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value1);
    }
}
