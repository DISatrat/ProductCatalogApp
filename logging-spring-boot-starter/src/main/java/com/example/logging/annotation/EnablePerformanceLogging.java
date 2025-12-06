package com.example.logging.annotation;

import com.example.logging.config.PerformanceLoggingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для включения функциональности логирования производительности.
 * <p>
 * Должна быть размещена на классе конфигурации приложения.
 * </p>
 * <p>
 * Пример использования:
 * <pre>
 * &#064;SpringBootApplication
 * &#064;EnablePerformanceLogging
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PerformanceLoggingConfiguration.class)
public @interface EnablePerformanceLogging {
}
