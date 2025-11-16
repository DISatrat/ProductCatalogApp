package factory;

import repository.user.UserRepositoryImpl;

import java.sql.Connection;

/**
 * Фабрика для создания и управления пользователей.
 * Обеспечивает загрузку пользователей из постоянного хранилища и сохранение обратно.
 * Создает пользователя-администратора по умолчанию если данных нет.
 */
public class UserFactory {

    /**
     * Создает и инициализирует репозиторий пользователей для работы с БД.
     *
     * @param connection соединение с БД
     * @return инициализированный репозиторий пользователей
     */
    public static UserRepositoryImpl createUserRepository(Connection connection) {
        return new UserRepositoryImpl(connection);
    }
}