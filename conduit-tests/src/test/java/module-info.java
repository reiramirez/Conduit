open module conduit.tests {
    requires conduit.annotations;
    requires conduit.orm;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires org.junit.jupiter.engine;
    requires org.apache.logging.log4j;
    requires com.zaxxer.hikari;
    exports com.hayachikin.conduit.tests.integration.mysql8;
    exports com.hayachikin.conduit.tests.integration.mongodb;
    exports com.hayachikin.conduit.tests.integration.common.models;
    exports com.hayachikin.conduit.tests.integration.common.enums;
}