package service.audit;

import model.AuditEntry;

import java.util.List;

/**
 * Сервис для ведения журнала аудита действий пользователей.
 * Предоставляет методы для записи событий и получения записей аудита.
 */
public interface AuditService {

    /**
     * Записывает действие пользователя в журнал аудита и сохраняет в файл.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action тип выполненного действия
     * @param details дополнительные детали действия
     * @throws NullPointerException если любой из параметров равен null
     */

    void record(String username, String action, String details);

    /**
     * Возвращает все записи из журнала аудита.
     *
     * @return список всех записей аудита
     */
    List<AuditEntry> getEntries();

    /**
     * Добавляет запись аудита в файл для постоянного хранения.
     *
     * @param entry запись аудита для сохранения в файл
     */
}