package com.hayachikin.conduit.schema;

import com.hayachikin.conduit.DataType;

public class ForeignAttribute extends Attribute {
    private final Collection pairedCollection;
    public ForeignAttribute(String key, Attribute pairedAttribute, Collection pairedCollection) {
        super(key, pairedAttribute.dataType);

        this.pairedCollection = pairedCollection;
    }

    public Collection getPairedCollection() {
        return pairedCollection;
    }
}
