package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.Config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Менеджер пула соединений с базой данных
 */
public class ConnectionPoolManager {
    private static HikariDataSource dataSource;

    private ConnectionPoolManager() {
    }

    public static void initialize(Config config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getDbUrl());
        hikariConfig.setUsername(config.getDbUsername());
        hikariConfig.setPassword(config.getDbPassword());

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setAutoCommit(true);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Получить connection из пула
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Connection pool is not initialized. Call initialize() first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Закрыть пул соединений
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Connection pool closed successfully");
        }
    }

    /**
     * Получить статистику пула
     */
    public static void printPoolStats() {
        if (dataSource != null) {
            System.out.println("Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Idle connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Total connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
        }
    }
}