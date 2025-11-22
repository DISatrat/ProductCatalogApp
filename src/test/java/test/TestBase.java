package test;

import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestBase {

    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    protected static Connection connection;
    protected static Long testUserId;

    public static void setUpAll() {
        postgres.start();

        try {
            connection = DriverManager.getConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword()
            );

            setupDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup test database", e);
        }
    }

    public static void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (postgres != null) {
            postgres.stop();
        }
    }

    private static void setupDatabase() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS app_schema");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app_schema.users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    user_role VARCHAR(20) DEFAULT 'USER',
                    created_at TIMESTAMP DEFAULT NOW()
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app_schema.products (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(200) NOT NULL,
                    category VARCHAR(100) NOT NULL,
                    brand VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    description TEXT,
                    user_id BIGINT NOT NULL,
                    created_at TIMESTAMP DEFAULT NOW(),
                    updated_at TIMESTAMP DEFAULT NOW()
                )
            """);

            stmt.execute("""
                INSERT INTO app_schema.users (username, password_hash, user_role)
                VALUES ('testuser', 'testhash', 'USER')
            """);

            var rs = stmt.executeQuery("SELECT id FROM app_schema.users WHERE username = 'testuser'");
            if (rs.next()) {
                testUserId = rs.getLong("id");
            }
        }
    }

    protected void clearProducts() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM app_schema.products");
        }
    }

    protected void clearUsers() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM app_schema.users WHERE username != 'testuser'");
        }
    }

    protected void clearAllData() throws Exception {
        clearProducts();
        clearUsers();
    }

    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    protected void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
}