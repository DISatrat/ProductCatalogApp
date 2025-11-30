package com.example.productcatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация для документации OpenAPI / Swagger.
 * <p>
 * Настраивает спецификацию OpenAPI для API Каталога Продуктов.
 * </p>
 *
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает bean конфигурации OpenAPI.
     *
     * @return настроенный экземпляр OpenAPI
     */
    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Каталога Продуктов")
                        .description("REST API для управления каталогом продуктов, пользователями и логами аудита")
                        .version("1.0.0")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Сервер разработки")
                ));
    }
}
