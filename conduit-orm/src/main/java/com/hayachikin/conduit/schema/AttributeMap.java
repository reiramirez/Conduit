package com.hayachikin.conduit.schema;

import java.util.List;

public record AttributeMap(List<Attribute> attributes,
                           List<Attribute> primaryAttributes) {
}
