package main.controller;

import main.model.User;
import main.service.user.UserService;
import main.service.audit.AuditService;
import java.util.Optional;

/**
 * Контроллер для управления аутентификацией и регистрацией пользователей.
 * Обеспечивает вход, выход и регистрацию с ведением журнала аудита.
 */
public class AuthController {
    /** Сервис для работы с пользователями */
    private final UserService userService;

    /** Сервис для записи действий в журнал аудита */
    private final AuditService auditService;

    /**
     * Конструктор контроллера аутентификации
     *
     * @param userService сервис для операций с пользователями
     * @param auditService сервис для записи действий аудита
     * @throws NullPointerException если любой из параметров равен null
     */
    public AuthController(UserService userService, AuditService auditService) {
        if (userService == null || auditService == null) {
            throw new NullPointerException("UserService and AuditService cannot be null");
        }
        this.userService = userService;
        this.auditService = auditService;
    }

    /**
     * Выполняет аутентификацию пользователя по логину и паролю.
     * При успешной аутентификации записывает событие в журнал аудита.
     *
     * @param username имя пользователя для входа
     * @param password пароль пользователя
     * @return Optional с объектом User при успешной аутентификации,
     *         или пустой Optional если аутентификация не удалась
     * @throws NullPointerException если username или password равны null
     */
    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new NullPointerException("Username and password cannot be null");
        }

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userService.checkPassword(password, userOpt.get().getPasswordHash())) {
            User user = userOpt.get();
            auditService.record(user.getUsername(), "LOGIN", "success");
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Проверяет уникальность имени пользователя и записывает событие в журнал аудита.
     *
     * @param username имя нового пользователя
     * @param password пароль нового пользователя
     * @return true если регистрация прошла успешно, false если пользователь уже существует
     * @throws NullPointerException если username или password равны null
     */
    public boolean register(String username, String password) {
        if (username == null || password == null) {
            throw new NullPointerException("Username and password cannot be null");
        }

        if (userService.findByUsername(username).isPresent()) {
            return false;
        }
        String hash = userService.hashPassword(password);
        User user = new User(username, hash);
        userService.addUser(user);
        auditService.record(username, "REGISTER", "created user");
        return true;
    }

    /**
     * Выполняет выход пользователя из системы.
     * Записывает событие выхода в журнал аудита.
     *
     * @param user пользователь, выполняющий выход
     * @throws NullPointerException если user равен null
     */
    public void logout(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        auditService.record(user.getUsername(), "LOGOUT", "user logged out");
    }
}