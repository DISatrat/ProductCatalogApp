package main.service.user;

import main.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями системы.
 * Предоставляет операции для регистрации, аутентификации и управления пользователями.
 */
public interface UserService {
    /**
     * Добавляет нового пользователя в систему.
     *
     * @param user объект пользователя для добавления
     */
    void addUser(User user);

    /**
     * Возвращает список всех пользователей системы.
     *
     * @return список всех пользователей
     */
    List<User> getUsers();

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с пользователем если найден, или пустой Optional если не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Хеширует пароль пользователя.
     *
     * @param pass пароль в открытом виде
     * @return хеш пароля
     */
    String hashPassword(String pass);

    /**
     * Проверяет соответствие пароля хешу.
     *
     * @param pass пароль в открытом виде
     * @param hash хеш пароля для проверки
     * @return true если пароль соответствует хешу, false в противном случае
     */
    boolean checkPassword(String pass, String hash);
}