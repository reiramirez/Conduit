package com.hayachikin.conduit.translation.impl;

import com.hayachikin.conduit.translation.ValueTransformer;

import java.util.UUID;

public class UUIDTransformer implements ValueTransformer<UUID, String> {
    @Override
    public UUID read(String output) {
        return UUID.fromString(output);
    }

    @Override
    public String write(UUID input) {
        return input.toString();
    }
}
