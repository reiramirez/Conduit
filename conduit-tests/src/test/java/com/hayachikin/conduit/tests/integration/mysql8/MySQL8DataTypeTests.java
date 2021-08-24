package com.hayachikin.conduit.tests.integration.mysql8;

import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.tests.integration.common.models.AllScalarDataTypesModel;
import com.hayachikin.conduit.tests.integration.common.enums.TestEnum;
import com.hayachikin.conduit.tests.integration.common.models.IntegerMapModel;
import com.hayachikin.conduit.tests.integration.common.models.UUIDModel;
import com.hayachikin.conduit.tests.integration.common.models.IntegerListModel;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Data type tests")
public class MySQL8DataTypeTests extends Base {

    @Test
    @DisplayName("Testing supported scalar data types")
    void testAllScalarDataTypesModel() throws ConduitException {
        db.mapClass(AllScalarDataTypesModel.class);
        db.initialize();

        int id = 1;
        AllScalarDataTypesModel p1 = new AllScalarDataTypesModel(id, (byte) 1, (short) 1, 1, 1L,
                true, 1.0F, 1.0, "one", TestEnum.A);
        db.upsert(AllScalarDataTypesModel.class, p1);

        AllScalarDataTypesModel p2 = db.select(AllScalarDataTypesModel.class, id);
        assertEquals(p1, p2, "All scalar data types should work");
        db.delete(AllScalarDataTypesModel.class, id);
    }

    @Test
    @DisplayName("Testing UUID data type")
    void testUUIDModel() throws ConduitException {
        db.mapClass(UUIDModel.class);
        db.initialize();

        UUID id = UUID.randomUUID();
        UUIDModel p1 = new UUIDModel(id);
        db.upsert(UUIDModel.class, p1);

        UUIDModel p2 = db.select(UUIDModel.class, id);
        assertEquals(p1, p2, "UUID should work");
        db.delete(UUIDModel.class, id);
    }

    @Test
    @DisplayName("Testing integer list data type")
    void testIntegerListModel() throws ConduitException {
        db.mapClass(IntegerListModel.class);
        db.initialize();

        ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(1, 2, 3));

        IntegerListModel p1 = new IntegerListModel(1, arrayList);
        db.upsert(IntegerListModel.class, p1);

        IntegerListModel p2 = db.select(IntegerListModel.class, 1);
        assertEquals(p1, p2);
        db.delete(IntegerListModel.class, 1);
    }

    @Test
    @DisplayName("Testing integer map data type")
    void testIntegerMapModel() throws ConduitException {
        db.mapClass(IntegerMapModel.class);
        db.initialize();

        HashMap<Integer, Integer> integerMap = new HashMap<>();
        integerMap.put(1, 2);
        integerMap.put(3, 4);
        integerMap.put(5, 6);

        IntegerMapModel p1 = new IntegerMapModel(1, integerMap);
        db.upsert(IntegerMapModel.class, p1);

        IntegerMapModel p2 = db.select(IntegerMapModel.class, 1);
        assertEquals(p1, p2);
        db.delete(IntegerMapModel.class, 1);
    }
}
