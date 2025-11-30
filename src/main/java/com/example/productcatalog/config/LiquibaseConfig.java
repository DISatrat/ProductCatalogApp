package com.example.productcatalog.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log:classpath:db/changelog/changelog-master.yaml}")
    private String changeLog;

    @Value("${spring.liquibase.default-schema:app_schema}")
    private String defaultSchema;

    @Value("${spring.liquibase.liquibase-schema:liquibase_schema}")
    private String liquibaseSchema;

    @Value("${spring.liquibase.contexts:dev}")
    private String contexts;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        liquibase.setContexts(contexts);
        return liquibase;
    }
}