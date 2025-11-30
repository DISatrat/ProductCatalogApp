package com.example.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения Каталога Продуктов на Spring Boot.
 * <p>
 * Этот класс служит точкой входа для приложения Spring Boot,
 * включая автоматическую конфигурацию, сканирование компонентов и функции Spring Boot.
 * </p>
 */
@SpringBootApplication
public class ProductCatalogApplication {

    /**
     * Точка входа приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}
