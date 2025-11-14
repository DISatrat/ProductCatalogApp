package main.repository;

import main.model.User;

import java.io.Serializable;
import java.util.*;

/**
 * Репозиторий для управления пользователями в памяти.
 * Обеспечивает базовые операции CRUD для пользователей с использованием Map для хранения.
 * Класс поддерживает сериализацию для сохранения состояния между запусками приложения.
 */
public class UserRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Внутреннее хранилище пользователей, где ключ - имя пользователя */
    private Map<String, User> users = new HashMap<>();

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с пользователем если найден, или пустой Optional если не найден
     * @throws NullPointerException если username равен null
     */
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            throw new NullPointerException("Username cannot be null");
        }
        return Optional.ofNullable(users.get(username));
    }

    /**
     * Добавляет нового пользователя в репозиторий.
     * Если пользователь с таким именем уже существует, операция игнорируется.
     *
     * @param user пользователь для добавления
     * @throws NullPointerException если user равен null
     */
    public void addUser(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        if (users.containsKey(user.getUsername())) {
            return;
        }
        users.put(user.getUsername(), user);
    }

    /**
     * Возвращает список всех пользователей в репозитории.
     *
     * @return список всех пользователей (может быть пустым, но не null)
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
