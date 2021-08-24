package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.ArrayList;
import java.util.Objects;

public class IntegerListModel {
    @Id
    int id;
    ArrayList<Integer> integerList;

    public IntegerListModel() {}

    public IntegerListModel(int id, ArrayList<Integer> integerList) {
        this.id = id;
        this.integerList = integerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerListModel that = (IntegerListModel) o;
        return id == that.id &&
                integerList.equals(that.integerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, integerList);
    }
}
