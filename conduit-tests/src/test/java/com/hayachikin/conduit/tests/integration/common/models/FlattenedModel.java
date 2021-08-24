package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Flattened;
import com.hayachikin.conduit.annotations.Id;

import java.util.Objects;

public class FlattenedModel {
    @Id
    private final int id;

    @Flattened
    private SimpleModel internalModel;

    public FlattenedModel(int id, SimpleModel internalModel) {
        this.id = id;
        this.internalModel = internalModel;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlattenedModel that = (FlattenedModel) o;
        return getId() == that.getId() && internalModel.equals(that.internalModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), internalModel);
    }
}
