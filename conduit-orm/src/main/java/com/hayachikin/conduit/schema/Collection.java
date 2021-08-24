package com.hayachikin.conduit.schema;

import com.hayachikin.conduit.annotations.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Collection {
    protected final String name;
    protected final boolean autoMigrated;
    protected AttributeMap attributeMap;

    public Collection(Class<?> objectClass) {
        if (objectClass.isAnnotationPresent(Schema.class)) {
            Schema schema = objectClass.getAnnotation(Schema.class);
            this.name = schema.name();
            this.autoMigrated = schema.autoMigrated();
        } else {
            this.name = objectClass.getSimpleName();
            this.autoMigrated = false;
        }
    }

    public String getName() { return name; }
    public boolean isAutoMigrated() {
        return autoMigrated;
    }
    public final List<Attribute> getAttributes() { return attributeMap.attributes().stream().map(a -> (Attribute) a.clone()).collect(Collectors.toList()); }
    public final List<Attribute> getFlattenedAttributes() { return flattenAttributes(getAttributes()); }
    public List<Attribute> getPrimaryAttributes() { return attributeMap.primaryAttributes().stream().map(a -> (Attribute) a.clone()).collect(Collectors.toList()); }
    public Attribute getAttribute(String key) { return getAttributes().stream().filter(a -> a.getKey().equals(key)).findFirst().orElse(null); }
    public List<String> getAttributeKeys() { return getAttributes().stream().map(Attribute::getKey).collect(Collectors.toList()); }
    public final List<String> getFlattenedAttributeKeys() { return getFlattenedAttributes().stream().map(Attribute::getKey).collect(Collectors.toList()); }

    // region Setters
    public final void setAttributeMap(AttributeMap attributeMap) {
        this.attributeMap = attributeMap;
    }
    // endregion

    private List<Attribute> flattenAttributes(List<Attribute> attributes) {
        List<Attribute> output = new ArrayList<>();
        for (Attribute attribute : attributes)
            if (attribute instanceof FlattenedAttribute flattenedAttribute)
                output.addAll(flattenAttributes(flattenedAttribute.internalAttributes));
            else
                output.add(attribute);
        return output;
    }
}
