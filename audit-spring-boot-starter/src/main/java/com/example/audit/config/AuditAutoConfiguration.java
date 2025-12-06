package com.example.audit.config;

import com.example.audit.aspect.AuditAspect;
import com.example.audit.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Автоконфигурация для модуля аудита.
 * <p>
 * Автоматически включается при наличии зависимости audit-spring-boot-starter в classpath.
 * Регистрирует AuditAspect если не определен в приложении.
 * </p>
 */
@Slf4j
@AutoConfiguration
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(AuditService auditService) {
        log.info("Инициализирована автоконфигурация Audit модуля");
        return new AuditAspect(auditService);
    }
}
