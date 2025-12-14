package com.example.productcatalog;

import com.example.logging.annotation.EnablePerformanceLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePerformanceLogging
public class ProductCatalogApp {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApp.class, args);
    }
}