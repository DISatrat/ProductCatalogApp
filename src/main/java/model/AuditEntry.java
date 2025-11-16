package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Представляет запись в журнале аудита для отслеживания действий пользователей.
 * Содержит информацию о времени выполнения действия, пользователе, типе действия и деталях.
 * Класс поддерживает сериализацию для данного хранения.
 */
public class AuditEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор аудита
     */
    private Long id;

    /**
     * Временная метка выполнения действия
     */
    private Date timestamp;

    /**
     * Имя пользователя, выполнившего действие
     */
    private String username;

    /**
     * Тип выполненного действия (например, LOGIN, ADD_PRODUCT, SEARCH)
     */
    private String action;

    /**
     * Дополнительные детали действия
     */
    private String details;

    /**
     * Создает новую запись аудита с текущей временной меткой.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action   тип выполненного действия
     * @param details  дополнительные детали действия
     * @throws NullPointerException если username, action или details равны null
     */
    public AuditEntry(String username, String action, String details) {
        if (username == null || action == null || details == null) {
            throw new NullPointerException("Username, action and details cannot be null");
        }

        this.timestamp = new Date();
        this.username = username;
        this.action = action;
        this.details = details;
    }

    public AuditEntry() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Возвращает строковое представление записи аудита в формате:
     * [timestamp] user=username action=action details=details
     */
    @Override
    public String toString() {
        return String.format("[%s] user=%s action=%s details=%s",
                timestamp.toString(), username, action, details);
    }
}
