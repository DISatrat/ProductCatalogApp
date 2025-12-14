package com.example.productcatalog.service.audit;

import com.example.productcatalog.model.AuditEntry;

import java.util.List;

/**
 * Интерфейс сервиса для операций аудита.
 * Расширяет базовый интерфейс из audit-spring-boot-starter.
 */
public interface AuditService extends com.example.audit.service.AuditService {

    /**
     * Получает все записи аудита.
     *
     * @return список всех записей аудита
     */
    List<AuditEntry> getAllEntries();

    /**
     * Получает недавние записи аудита.
     *
     * @param limit максимальное количество записей для получения
     * @return список недавних записей аудита
     */
    List<AuditEntry> getRecentEntries(int limit);
}
