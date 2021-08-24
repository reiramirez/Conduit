package com.hayachikin.conduit.schema;

import com.hayachikin.conduit.DataType;

import java.util.Objects;

public class Attribute implements Cloneable {
    protected final String key;
    protected final DataType dataType;

    public Attribute(String key, DataType dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() { return key; }
    public DataType getDataType() { return dataType; }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return key.equals(attribute.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
