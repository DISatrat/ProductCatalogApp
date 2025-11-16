package config;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseMigrator {

    public void runMigrations(Config config) {
        try (Connection connection = DriverManager.getConnection(
                config.getDbUrl(),
                config.getDbUsername(),
                config.getDbPassword())) {

            createSchemas(connection, config);

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setLiquibaseSchemaName(config.getLiquibaseSchema());
            database.setDefaultSchemaName(config.getDefaultSchema());

            Liquibase liquibase = new Liquibase(
                    config.getLiquibaseChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(config.getLiquibaseContexts());
            System.out.println("Migrations completed successfully");

        } catch (Exception e) {
            throw new RuntimeException("Migration failed", e);
        }
    }

    private void createSchemas(Connection connection, Config config) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + config.getLiquibaseSchema());
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + config.getDefaultSchema());
            System.out.println("Schemas created: " + config.getLiquibaseSchema() + ", " + config.getDefaultSchema());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create schemas", e);
        }
    }
}