package com.hayachikin.conduit.translation;

import com.hayachikin.conduit.exceptions.ConduitMappingException;

public interface ValueTransformer<I, O> {
    I read(O output) throws ConduitMappingException;
    O write(I input) throws ConduitMappingException;
}
