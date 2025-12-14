package com.example.productcatalog.repository.audit;

import com.example.productcatalog.model.AuditEntry;

import java.util.List;

/**
 * Интерфейс репозитория для операций с сущностью AuditEntry.
 */
public interface AuditRepository {

    /**
     * Записывает новую запись аудита.
     *
     * @param entry запись аудита для записи
     */
    void save(AuditEntry entry);

    /**
     * Получает все записи аудита, отсортированные по временной метке в обратном порядке.
     *
     * @return список записей аудита
     */
    List<AuditEntry> findAll();

    /**
     * Получает самые недавние записи аудита.
     *
     * @param limit максимальное количество записей для получения
     * @return список недавних записей аудита
     */
    List<AuditEntry> findRecent(int limit);
}
