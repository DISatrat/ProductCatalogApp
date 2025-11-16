package repository.user;

import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями в базе данных.
 * Обеспечивает базовые операции CRUD для пользователей.
 */
public interface UserRepository {

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с пользователем если найден, или пустой Optional если не найден
     * @throws NullPointerException если username равен null
     */
    Optional<User> findByUsername(String username);

    /**
     * Добавляет нового пользователя в репозиторий.
     *
     * @param user пользователь для добавления
     * @throws NullPointerException если user равен null
     */
    void addUser(User user);

    /**
     * Возвращает список всех пользователей в репозитории.
     *
     * @return список всех пользователей (может быть пустым, но не null)
     */
    List<User> findAll();
}