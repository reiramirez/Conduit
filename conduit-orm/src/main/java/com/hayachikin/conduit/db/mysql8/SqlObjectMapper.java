package com.hayachikin.conduit.db.mysql8;

import com.hayachikin.conduit.ObjectMapper;
import com.hayachikin.conduit.translation.impl.UUIDTransformer;

import java.util.UUID;

public final class SqlObjectMapper extends ObjectMapper {
    public SqlObjectMapper() {
        super();
        addValueTransformer(UUID.class, new UUIDTransformer());
    }
}
