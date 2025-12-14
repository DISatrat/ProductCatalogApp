package com.example.logging.config;

import com.example.logging.aspect.PerformanceLoggingAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для логирования производительности.
 * <p>
 * Регистрирует PerformanceLoggingAspect при наличии аннотации @EnablePerformanceLogging.
 * </p>
 */
@Slf4j
@Configuration
public class PerformanceLoggingConfiguration {

    @Bean
    public PerformanceLoggingAspect performanceLoggingAspect() {
        log.info("Инициализирована конфигурация Performance Logging модуля");
        return new PerformanceLoggingAspect();
    }
}
