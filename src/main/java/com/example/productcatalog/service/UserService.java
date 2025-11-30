package com.example.productcatalog.service;

import com.example.productcatalog.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для операций с пользователями.
 */
public interface UserService {

    /**
     * Находит пользователя по имени.
     *
     * @param username имя для поиска
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Регистрирует нового пользователя.
     *
     * @param username имя пользователя
     * @param password открытый пароль
     * @return созданный пользователь
     */
    User register(String username, String password);

    /**
     * Получает всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Проверяет, существует ли пользователь по имени.
     *
     * @param username имя для проверки
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Хеширует пароль.
     *
     * @param password открытый пароль
     * @return хеш пароля
     */
    String hashPassword(String password);

    /**
     * Проверяет пароль по его хешу.
     *
     * @param password открытый пароль
     * @param hash     хеш пароля
     * @return true, если пароль совпадает
     */
    boolean checkPassword(String password, String hash);
}
