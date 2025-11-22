package factory;

import repository.audit.AuditRepositoryImpl;


/**
 * Фабрика для создания и управления аудитом.
 * Обеспечивает загрузку товаров из постоянного хранилища и сохранение обратно.
 */
public class AuditFactory {
    /**
     * Создает и инициализирует репозиторий товаров для работы с БД.
     *
     * @return инициализированный репозиторий товаров
     */
    public static AuditRepositoryImpl createAuditRepository() {
        return new AuditRepositoryImpl();
    }
}
