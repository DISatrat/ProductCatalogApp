package controller;

import model.User;
import model.enums.UserRole;
import service.user.UserService;
import java.util.List;

/**
 * Контроллер для управления операциями с пользователями.
 * Обеспечивает контроль доступа к функциям управления пользователями
 * на основе ролей пользователей.
 */
public class UserController {
    /** Сервис для операций с пользователями */
    private final UserService userService;

    /**
     * Конструктор контроллера пользователей
     *
     * @param userService сервис для операций с пользователями
     * @throws NullPointerException если userService равен null
     */
    public UserController(UserService userService) {
        if (userService == null) {
            throw new NullPointerException("UserService cannot be null");
        }
        this.userService = userService;
    }

    /**
     * Возвращает список всех пользователей системы.
     * Доступ к этому методу ограничен пользователями с ролью ADMIN.
     *
     * @param currentUser пользователь, выполняющий запрос
     * @return список всех пользователей системы (может быть пустым, но не null)
     * @throws SecurityException если текущий пользователь не имеет роли ADMIN
     * @throws NullPointerException если currentUser равен null
     */
    public List<User> getUsers(User currentUser) {
        if (currentUser == null) {
            throw new NullPointerException("Current user cannot be null");
        }

        if (currentUser.getUserRole() != UserRole.ADMIN) {
            throw new SecurityException("Доступ запрещен! Требуется роль ADMIN.");
        }

        return userService.getUsers();
    }
}