package config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;
public class Config {
    private Map<String, Object> config;

    public Config(String configFile) {
        Yaml yaml = new Yaml();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new RuntimeException("Config file not found: " + configFile);
            }
            config = yaml.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config", e);
        }
    }

    public String getDbUrl() {
        Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
        return String.format("jdbc:postgresql://%s:%d/%s?currentSchema=%s",
                dbConfig.get("host"),
                dbConfig.get("port"),
                dbConfig.get("name"),
                dbConfig.get("defaultSchema"));
    }

    public String getDbUsername() {
        Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
        return (String) dbConfig.get("username");
    }

    public String getDbPassword() {
        Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
        return (String) dbConfig.get("password");
    }

    public String getDefaultSchema() {
        Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
        return (String) dbConfig.get("defaultSchema");
    }

    public String getLiquibaseChangeLog() {
        Map<String, Object> migrationsConfig = (Map<String, Object>) config.get("migrations");
        return (String) migrationsConfig.get("change-log");
    }

    public String getLiquibaseContexts() {
        Map<String, Object> migrationsConfig = (Map<String, Object>) config.get("migrations");
        return (String) migrationsConfig.get("contexts");
    }

    public String getLiquibaseSchema() {
        Map<String, Object> migrationsConfig = (Map<String, Object>) config.get("migrations");
        Map<String, Object> liquibaseConfig = (Map<String, Object>) migrationsConfig.get("liquibase");
        return (String) liquibaseConfig.get("schema");
    }
}
