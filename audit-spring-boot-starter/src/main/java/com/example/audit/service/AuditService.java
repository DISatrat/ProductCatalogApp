package com.example.audit.service;

/**
 * Интерфейс для сервиса аудита.
 */
public interface AuditService {
    /**
     * Записывает запись аудита.
     *
     * @param username имя пользователя
     * @param action   действие
     * @param details  детали
     */
    void record(String username, String action, String details);
}
