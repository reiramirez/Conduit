package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.HashMap;
import java.util.Objects;

public class IntegerMapModel {
    @Id
    int id;
    HashMap<Integer, Integer> integerMap;

    public IntegerMapModel() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerMapModel that = (IntegerMapModel) o;
        return id == that.id &&
                integerMap.equals(that.integerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, integerMap);
    }

    public IntegerMapModel(int id, HashMap<Integer, Integer> integerMap) {
        this.id = id;
        this.integerMap = integerMap;
    }
}
