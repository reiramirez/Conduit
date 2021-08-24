module conduit.orm {
    requires conduit.annotations;
    requires org.objenesis;
    requires org.apache.logging.log4j;
    requires static com.zaxxer.hikari;
    requires static java.sql;
    requires static org.mongodb.driver.sync.client;
    requires static org.mongodb.driver.core;
    exports com.hayachikin.conduit;
    exports com.hayachikin.conduit.exceptions;
    exports com.hayachikin.conduit.mapping;
    exports com.hayachikin.conduit.translation;
    exports com.hayachikin.conduit.db;
}