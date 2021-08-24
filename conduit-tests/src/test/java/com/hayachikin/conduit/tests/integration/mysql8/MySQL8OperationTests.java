package com.hayachikin.conduit.tests.integration.mysql8;

import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.tests.integration.common.models.SimpleModel;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Operation tests")
public class MySQL8OperationTests extends Base {
    SimpleModel model;

    @BeforeEach
    void prepare() throws ConduitException {
        // map models
        db.mapClass(SimpleModel.class);
        db.initialize();
    }

    @Test
    @Order(1)
    @Disabled("Not implemented yet")
    public void insertTest() {
        model = new SimpleModel(1, "test");


    }

    @Test
    @Order(2)
    @Disabled("Not implemented yet")
    public void updateTest() {

    }

    @Test
    @Order(3)
    public void upsertTest() {

    }

    @Test
    @Order(4)
    public void selectTest() {

    }

    @Test
    @Order(5)
    public void deleteTest() {

    }
}
