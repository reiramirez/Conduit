package com.hayachikin.conduit.tests.integration.mysql8;

import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.tests.integration.common.models.SimpleModel;
import com.hayachikin.conduit.tests.integration.common.models.AbstractModelSubclass;
import com.hayachikin.conduit.tests.integration.common.models.FlattenedModel;
import com.hayachikin.conduit.tests.integration.common.models.SimpleModelSubclass;
import com.hayachikin.conduit.tests.integration.common.models.SimpleModelNoDefaultConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Model tests")
public class MySQLModel8Tests extends Base {
    @Test
    @DisplayName("Testing simple model without inheritance")
    void testSimpleModel() throws ConduitException {
        db.mapClass(SimpleModel.class);
        db.initialize();

        String value1 = "value1";
        SimpleModel p1 = new SimpleModel(1, value1);
        assertNotNull(p1, "Simple model should exist");
        db.upsert(SimpleModel.class, p1);

        SimpleModel p2 = db.select(SimpleModel.class, p1.getId());
        assertEquals(p1, p2, "Simple model should work");
        db.delete(SimpleModel.class, p1.getId());
    }

    @Test
    @DisplayName("Testing simple model with simple superclass")
    void testSimpleModelWithSimpleSuperclass() throws ConduitException {
        db.mapClass(SimpleModelSubclass.class);
        db.initialize();

        String value1 = "value1", value2 = "value2";
        SimpleModelSubclass p1 = new SimpleModelSubclass(1, value1, value2);
        db.upsert(SimpleModelSubclass.class, p1);
        assertEquals(p1, db.select(SimpleModelSubclass.class, p1.getId()));
        db.delete(SimpleModelSubclass.class, p1.getId());
    }

    @Test
    @DisplayName("Testing simple model with abstract superclass")
    void testSimpleModelWithAbstractSuperclass() throws ConduitException {
        db.mapClass(AbstractModelSubclass.class);
        db.initialize();

        String value1 = "value1", value2 = "value2";
        AbstractModelSubclass p1 = new AbstractModelSubclass(1, value1);
        db.upsert(AbstractModelSubclass.class, p1);
        assertEquals(p1, db.select(AbstractModelSubclass.class, p1.getId()));
        db.delete(AbstractModelSubclass.class, p1.getId());
    }

    @Test
    @DisplayName("Testing simple model without default constructor")
    void testSimpleModelNoDefaultConstructor() throws ConduitException {
        db.mapClass(SimpleModelNoDefaultConstructor.class);
        db.initialize();

        SimpleModelNoDefaultConstructor p1 = new SimpleModelNoDefaultConstructor(1);
        db.upsert(SimpleModelNoDefaultConstructor.class, p1);
        assertEquals(p1, db.select(SimpleModelNoDefaultConstructor.class, p1.getId()));
        db.delete(SimpleModelNoDefaultConstructor.class, p1.getId());
    }
    @Test
    @DisplayName("Testing flattened model")
    void testFlattenedModel() throws ConduitException {
        db.mapClass(FlattenedModel.class);
        db.initialize();

        SimpleModel simpleModel = new SimpleModel(99, "one");
        FlattenedModel p1 = new FlattenedModel(1, simpleModel);
        db.upsert(FlattenedModel.class, p1);
        FlattenedModel p2 = db.select(FlattenedModel.class, p1.getId());
        assertEquals(p1, p2, "Flattened fields are not working");
        db.delete(FlattenedModel.class, p1.getId());
    }
}
