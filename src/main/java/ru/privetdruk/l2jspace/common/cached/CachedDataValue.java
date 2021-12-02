package ru.privetdruk.l2jspace.common.cached;

import ru.privetdruk.l2jspace.common.logging.CLogger;
import ru.privetdruk.l2jspace.common.pool.ConnectionPool;

import java.sql.SQLException;

public class CachedDataValue {
    public static final CLogger LOGGER = new CLogger(CachedDataValue.class.getName());

    private static final String LOAD_QUERY = "SELECT `valueData` FROM `character_data` WHERE `valueName`='%s' AND `charId`=%d LIMIT 1";
    private static final String UPDATE_QUERY = "INSERT INTO `character_data` (`charId`, `valueName`, `valueData`) VALUES (%d, '%s', ?) ON DUPLICATE KEY UPDATE `valueData`=?";

    private final CachedDataKey key;
    private final String compiledLoadQuery;
    private final String compiledUpdateQuery;
    private volatile String valueData;
    private volatile boolean dirty = false;

    CachedDataValue(CachedDataKey key, String defaultValue, int charId) {
        this.key = key;
        valueData = defaultValue;
        compiledLoadQuery = String.format(LOAD_QUERY, key, charId);
        compiledUpdateQuery = String.format(UPDATE_QUERY, charId, key);
    }

    public CachedDataKey getKey() {
        return key;
    }

    synchronized void update() {
        if (dirty) {
            save();
            dirty = false;
        }
    }

    void save() {
        try (var conn = ConnectionPool.getConnection();
             var stmt = conn.prepareStatement(compiledUpdateQuery)) {
            stmt.setString(1, valueData);
            stmt.setString(2, valueData);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed save({}) character_data", e, key);
        }
    }

    void load() {
        try (var connection = ConnectionPool.getConnection();
             var statement = connection.prepareStatement(compiledLoadQuery)) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    valueData = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed load({}) character_data", e, key);
        }
    }

    public synchronized void setValue(String value) {
        valueData = value;
        dirty = true;
    }

    public String getValue() {
        return valueData;
    }
}