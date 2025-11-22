package factory;

import repository.user.UserRepositoryImpl;

/**
 * Фабрика для создания и управления пользователей.
 * Обеспечивает загрузку пользователей из постоянного хранилища и сохранение обратно.
 * Создает пользователя-администратора по умолчанию если данных нет.
 */
public class UserFactory {

    /**
     * Создает и инициализирует репозиторий пользователей для работы с БД.
     *
     * @return инициализированный репозиторий пользователей
     */
    public static UserRepositoryImpl createUserRepository() {
        return new UserRepositoryImpl();
    }
}