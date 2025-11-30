package com.example.productcatalog.service;

import com.example.productcatalog.model.AuditEntry;

import java.util.List;

/**
 * Интерфейс сервиса для операций аудита.
 */
public interface AuditService {

    /**
     * Записывает запись аудита.
     *
     * @param username имя пользователя, выполняющего действие
     * @param action   тип действия
     * @param details  дополнительные подробности о действии
     */
    void record(String username, String action, String details);

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
