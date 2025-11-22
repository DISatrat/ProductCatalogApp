package util;

/**
 * Класс констант для SQL запросов
 */
public final class SQLConstants {

    public static final String SCHEMA = "app_schema";

    public static final class Audit {
        public static final String TABLE = SCHEMA + ".audit_entries";
        public static final String INSERT = "INSERT INTO " + TABLE + " (timestamp, username, action, details) VALUES (?, ?, ?, ?)";
        public static final String SELECT_ALL = "SELECT id, timestamp, username, action, details FROM " + TABLE + " ORDER BY timestamp DESC";
        public static final String COLUMNS = "id, timestamp, username, action, details";
    }

    public static final class User {
        public static final String TABLE = SCHEMA + ".users";
        public static final String INSERT = "INSERT INTO " + TABLE + " (username, password_hash, user_role) VALUES (?, ?, ?)";
        public static final String SELECT_BY_USERNAME = "SELECT id, username, password_hash, user_role FROM " + TABLE + " WHERE username = ?";
        public static final String SELECT_ALL = "SELECT id, username, password_hash, user_role FROM " + TABLE;
        public static final String COLUMNS = "id, username, password_hash, user_role";
    }

    public static final class Product {
        public static final String TABLE = SCHEMA + ".products";
        public static final String INSERT = "INSERT INTO " + TABLE + " (name, category, brand, price, description, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        public static final String UPDATE = "UPDATE " + TABLE + " SET name = ?, category = ?, brand = ?, price = ?, description = ?, updated_at = NOW() WHERE id = ?";
        public static final String DELETE = "DELETE FROM " + TABLE + " WHERE id = ?";
        public static final String SELECT_BY_ID = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM " + TABLE + " WHERE id = ?";
        public static final String SELECT_ALL = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM " + TABLE + " ORDER BY id";
        public static final String COUNT = "SELECT COUNT(*) FROM " + TABLE;
        public static final String COLUMNS = "id, name, category, brand, price, description, created_at, updated_at";
        public static final String BASE_SEARCH = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE 1=1";
    }

    private SQLConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}