package controller;

import model.AuditEntry;
import service.audit.AuditService;
import java.util.List;

/**
 * Контроллер для управления операциями аудита.
 * Предоставляет методы для получения записей аудита с учетом ограничений доступа.
 */
public class AuditController {
    /** Сервис аудита для доступа к данным журнала аудита */
    private final AuditService auditService;

    /**
     * Конструктор контроллера аудита
     *
     * @param auditService сервис аудита для работы с записями журнала
     * @throws NullPointerException если auditService равен null
     */
    public AuditController(AuditService auditService) {
        if (auditService == null) {
            throw new NullPointerException("AuditService cannot be null");
        }
        this.auditService = auditService;
    }

    /**
     * Возвращает список последних записей аудита.
     * Если запрошенное количество превышает общее количество записей,
     * возвращаются все доступные записи.
     *
     * @param count количество последних записей для возврата
     * @return список последних записей аудита (может быть пустым, но не null)
     * @throws IllegalArgumentException если count отрицательный
     */
    public List<AuditEntry> getRecentAuditEntries(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        List<AuditEntry> allEntries = auditService.getEntries();
        int fromIndex = Math.max(0, allEntries.size() - count);
        return allEntries.subList(fromIndex, allEntries.size());
    }
}