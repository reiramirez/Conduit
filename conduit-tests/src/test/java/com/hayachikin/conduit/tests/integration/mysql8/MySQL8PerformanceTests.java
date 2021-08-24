package com.hayachikin.conduit.tests.integration.mysql8;

import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.tests.integration.common.models.SimpleModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Disabled
@DisplayName("Performance tests")
public class MySQL8PerformanceTests extends Base {
    @BeforeEach
    void prepare() throws ConduitException {
        // map models
        db.mapClass(SimpleModel.class);
        db.initialize();

        // warmup
        db.upsert(SimpleModel.class, new SimpleModel(Integer.MAX_VALUE, "warmup"));
    }

    @ParameterizedTest
    @DisplayName("Testing simple model upsert performance")
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 100000})
    void testSimpleModelUpsert(int modelCount) throws ConduitException {
        SimpleModel[] models = new SimpleModel[modelCount];
        for (int c = 0; c < modelCount; c++)
            models[c] = new SimpleModel(c + 1, c + "");

        for (SimpleModel model : models)
            db.upsert(SimpleModel.class, model);
    }
}
