package com.hayachikin.conduit.tests.integration.common.models;

import com.hayachikin.conduit.annotations.Id;
import com.hayachikin.conduit.tests.integration.common.enums.TestEnum;

import java.util.Objects;

public class AllScalarDataTypesModel {
    @Id
    private final int id;

    private final byte byteValue;
    private final short shortValue;
    private final int intValue;
    private final long longValue;
    private final boolean booleanValue;
    private final float floatValue;
    private final double doubleValue;
    private final String stringValue;
    private final TestEnum enumValue;

    public AllScalarDataTypesModel(int id, byte byteValue, short shortValue, int intValue, long longValue, boolean booleanValue, float floatValue, double doubleValue, String stringValue, TestEnum enumValue) {
        this.id = id;
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.booleanValue = booleanValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
        this.enumValue = enumValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllScalarDataTypesModel that = (AllScalarDataTypesModel) o;
        return id == that.id && byteValue == that.byteValue && shortValue == that.shortValue && intValue == that.intValue && longValue == that.longValue && booleanValue == that.booleanValue && Float.compare(that.floatValue, floatValue) == 0 && Double.compare(that.doubleValue, doubleValue) == 0 && stringValue.equals(that.stringValue) && enumValue == that.enumValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, byteValue, shortValue, intValue, longValue, booleanValue, floatValue, doubleValue, stringValue, enumValue);
    }
}
