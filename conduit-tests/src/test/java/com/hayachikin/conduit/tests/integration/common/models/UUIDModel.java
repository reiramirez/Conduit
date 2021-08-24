package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;

import java.util.Objects;
import java.util.UUID;

public class UUIDModel {
    @Id
    UUID id;

    public UUIDModel() {}

    public UUIDModel(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UUIDModel uuidModel = (UUIDModel) o;
        return Objects.equals(id, uuidModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
