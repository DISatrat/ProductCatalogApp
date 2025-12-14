package com.example.productcatalog.repository.user;

import com.example.productcatalog.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для операций с сущностью User.
 */
public interface UserRepository {

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Добавляет нового пользователя в базу данных.
     *
     * @param user пользователь для добавления
     */
    void save(User user);

    /**
     * Получает всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Проверяет, существует ли пользователь по имени.
     *
     * @param username имя пользователя для проверки
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);
}
