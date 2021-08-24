package com.hayachikin.conduit.schema;

import com.hayachikin.conduit.DataType;

import java.util.List;
import java.util.stream.Collectors;

public class FlattenedAttribute extends Attribute {
    public final List<Attribute> internalAttributes;

    public FlattenedAttribute(String key, List<Attribute> internalAttributes) {
        super(key, DataType.FLATTENED);
        this.internalAttributes = internalAttributes.stream().map(internalAttribute -> new Attribute(key + "." + internalAttribute.key, internalAttribute.dataType)).collect(Collectors.toList());
    }
}
