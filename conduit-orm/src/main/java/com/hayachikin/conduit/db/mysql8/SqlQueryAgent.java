package com.hayachikin.conduit.db.mysql8;

import com.hayachikin.conduit.*;
import com.hayachikin.conduit.exceptions.ConduitException;
import com.hayachikin.conduit.exceptions.ConduitQueryAgentException;
import com.hayachikin.conduit.DataType;
import com.hayachikin.conduit.db.QueryAgent;
import com.hayachikin.conduit.schema.Attribute;
import com.hayachikin.conduit.schema.Collection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public final class SqlQueryAgent extends QueryAgent {
    private final HikariConfig cfg;
    private final HikariDataSource ds;

    public SqlQueryAgent(String databaseName, DatabaseOptions dbOptions) throws ConduitException {
        super(databaseName, dbOptions);

        cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://" + dbOptions.getHost() + ":" + dbOptions.getPort() + "/" + databaseName + "?createDatabaseIfNotExist=true");
        cfg.setUsername(dbOptions.getUsername());
        cfg.setPassword(dbOptions.getPassword());

        // TODO: add to config
        cfg.setConnectionTimeout(20000);
        cfg.setMinimumIdle(10);
        cfg.setMaximumPoolSize(20);
        cfg.setIdleTimeout(300000);
        cfg.addDataSourceProperty("prepStmtCacheSize", 250);
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        cfg.addDataSourceProperty("cachePrepStmts", true);
        ds = new HikariDataSource(cfg);
    }

    @Override
    public void createCollection(Collection collection) throws ConduitQueryAgentException {
        if (collectionExists(collection)) {
            if (collection.isAutoMigrated()) {
                // TODO: handle migration
                return;
            }
            else deleteCollection(collection); // don't migrate: overwrite
        }

        StringJoiner sj = new StringJoiner(", ", "CREATE " + (dbOptions.isTemporaryCollections() ? "TEMPORARY " : "") +
                "TABLE IF NOT EXISTS `" + collection.getName() + "` (", ");");

        // region Attribute mapping
        for (Attribute attribute : collection.getFlattenedAttributes()) {
            DataType dataType = attribute.getDataType();

            sj.add("`" + attribute.getKey() + "` " + toMySQL8DataType(dataType));
        }
        sj.add("PRIMARY KEY (`" + collection.getPrimaryAttributes().stream().map(Attribute::getKey).collect(Collectors.joining("`, `")) + "`)");
        // endregion

        String query = sj.toString();

        try(Connection conn = ds.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            logger.error("Error while executing SQL query: " + query, e);
        }
    }

    @Override
    public boolean collectionExists(Collection collection) {
        // TODO: implement versioning
        
        String query = "SHOW TABLES LIKE ?";
        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, collection.getName());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            // TODO: implement logging
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void deleteCollection(Collection collection) {
        String query = "DROP TABLE IF EXISTS " + collection.getName();

        try(Connection conn = ds.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HashMap<String, Object>> select(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException {
        final boolean hasFilter = queryFilter != null;
        String query = "SELECT `" + String.join("`, `", collection.getFlattenedAttributeKeys()) + "` FROM `" + collection.getName() + "`";

        if (hasFilter)
            query += formWhere(queryFilter);

        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            int colIndex = 1;
            if (hasFilter) {
                for (Map.Entry<String, Object> entry : queryFilter.entrySet()) {
                    ps.setObject(colIndex, entry.getValue());
                    colIndex++;
                }
            }

            ResultSet rs = ps.executeQuery();
            return convertResultSet(rs);

//                for (Attribute attribute : attributeMap.getExternallyStoredAttributes()) {
//                    for (T object : objects) {
//
//                    }
//                }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void insert(Collection collection, List<HashMap<String, Object>> dataMaps)  throws ConduitQueryAgentException {

    }

    @Override
    public void update(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException {

    }

    @Override
    public void upsert(Collection collection, List<HashMap<String, Object>> dataMaps) throws ConduitQueryAgentException {
        if (dataMaps.size() == 0) return; // no-op

        List<Attribute> attributes = collection.getFlattenedAttributes();
        List<String> attributeKeys = attributes.stream().map(Attribute::getKey).collect(Collectors.toList());
        String  prefix = "INSERT INTO `" + collection.getName() +  "` (`" + String.join("`, `", attributeKeys) + "`) VALUES ",
                suffix = " ON DUPLICATE KEY UPDATE " +
                        attributeKeys.stream().map(key -> "`" + key + "` = VALUES(`" + key + "`)").collect(Collectors.joining(", "));
        StringJoiner sj = new StringJoiner(",\n", prefix, suffix);
        for (int i = 0; i < dataMaps.size(); i++)
            sj.add("(" + String.join(", ", Collections.nCopies(attributes.size(), "?")) + ")");
        String query = sj.toString();

        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {

            int i = 1;
            for (HashMap<String, Object> dataMap : dataMaps) {
                for (String orderedKey : attributeKeys) {
                    ps.setObject(i, dataMap.get(orderedKey));
                    i++;
                }
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(Collection collection, HashMap<String, Object> queryFilter) throws ConduitQueryAgentException {
        String query = "DELETE FROM " + collection.getName() + formWhere(queryFilter);

        try(Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            int colIndex = 1;
            for (Map.Entry<String, Object> entry : queryFilter.entrySet()) {
                ps.setObject(colIndex, entry.getValue());
                colIndex++;
            }

            ps.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String formWhere(HashMap<String, Object> filter) {
        if (filter.entrySet().isEmpty())
            return "";

        StringJoiner sj = new StringJoiner(" AND ", " WHERE (", ")");

        for (String key : filter.keySet()) {
            sj.add(key + " = ?");
        }

        return sj.toString();
    }

    private String toMySQL8DataType(DataType dataType) throws ConduitQueryAgentException {
        switch (dataType) {
            case BYTE:
                return "TINYINT";
            case SHORT:
                return "SMALLINT";
            case INTEGER:
                return "INT";
            case LONG:
                return "BIGINT";
            case FLOAT:
                return "FLOAT";
            case DOUBLE:
                return "DOUBLE";
            case STRING:
                return "VARCHAR(255)";
            case UUID: case ENUM:
                return "VARCHAR(36)";
            case BOOLEAN:
                return "BOOL";
        }

        throw new ConduitQueryAgentException("Unhandled data type: " + dataType);
    }

//    private void createTable(String tblName, boolean temporary) {
//        StringJoiner sj = new StringJoiner(", ", "CREATE " + (temporary ? "TEMPORARY " : "") +
//                "TABLE IF NOT EXISTS " + tblName + " (", ");");
//
//        for (Map.Entry<String, DataType> entry : attributeSchema.entrySet()) {
//
//            // TODO: recursive table creation
//            sj.add(entry.getKey() + " " + entry.getValue());
//        }
//
//        sj.add("PRIMARY KEY (" + attributeSchema.getPrimaryKey() + ")");
//        String query = sj.toString();
//
//        try(Connection conn = ds.getConnection();
//            Statement statement = conn.createStatement()) {
//            statement.execute(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private List<HashMap<String, Object>> convertResultSet(ResultSet rs) throws SQLException, ConduitQueryAgentException {
        List<HashMap<String, Object>> output = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        while (rs.next()) {
            HashMap<String, Object> dataMap = new HashMap<>();

            for (int c = 1; c <= cols; c++) {
                int columnType = md.getColumnType(c);
                String key = md.getColumnName(c);
                switch (columnType) {
                    case Types.BIGINT -> dataMap.put(key, rs.getLong(c));
                    case Types.INTEGER -> dataMap.put(key, rs.getInt(c));
                    case Types.SMALLINT, Types.TINYINT -> dataMap.put(key, rs.getShort(c));
                    case Types.VARCHAR -> dataMap.put(key, rs.getString(c));
                    case Types.BOOLEAN -> dataMap.put(key, rs.getBoolean(c));
                    default -> throw new ConduitQueryAgentException("Unsupported column type: " + md.getColumnTypeName(c));
                }
            }
            output.add(dataMap);
        }

        return output;
    }

}
