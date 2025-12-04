package com.example.productcatalog.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись журнала аудита.
 * <p>
 * Отслеживает действия пользователей в системе в целях безопасности и соответствия требованиям.
 * Каждая запись содержит информацию о действии, пользователе, который его выполнил,
 * и дополнительные подробности.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор записи аудита.
     */
    private Long id;

    /**
     * Временная метка выполнения действия.
     */
    private LocalDateTime timestamp;

    /**
     * Имя пользователя, выполнившего действие.
     */
    private String username;

    /**
     * Тип выполненного действия (например, LOGIN, CREATE_PRODUCT, SEARCH).
     */
    private String action;

    /**
     * Дополнительные подробности о действии.
     */
    private String details;

    /**
     * Создает новую запись аудита с текущей временной меткой.
     *
     * @param username имя пользователя, выполняющего действие
     * @param action   тип выполненного действия
     * @param details  дополнительные подробности о действии
     */
    public AuditEntry(String username, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.action = action;
        this.details = details;
    }
}
