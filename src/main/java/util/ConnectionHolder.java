package util;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHolder {

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        System.out.println("Closing database connection...");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            System.out.println("Database connection closed successfully");
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

}

