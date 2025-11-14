package main.model;

import main.model.enums.UserRole;

import java.io.Serializable;

/**
 * Представляет пользователя системы с учетными данными и ролью.
 * Содержит информацию для аутентификации и авторизации пользователя.
 * Класс поддерживает сериализацию для данного хранения.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Уникальный идентификатор пользователя */
    private Long id;

    /** Уникальное имя пользователя для входа в систему */
    private String username;

    /**
     * Хеш пароля пользователя.
     * Для примера используется простая хеш-функция или plain text.
     */
    private String passwordHash;

    /** Роль пользователя в системе определяет уровень доступа */
    private UserRole userRole;

    /**
     * Создает нового пользователя с ролью USER по умолчанию.
     *
     * @param username имя пользователя
     * @param passwordHash хеш пароля пользователя
     * @throws NullPointerException если username или passwordHash равны null
     */
    public User(String username, String passwordHash) {
        if (username == null || passwordHash == null) {
            throw new NullPointerException("Username and password hash cannot be null");
        }

        this.username = username;
        this.passwordHash = passwordHash;
        this.userRole = UserRole.USER;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getUserRole() { return userRole; }

    /**
     * Повышает права пользователя до роли ADMIN.
     * Используется для предоставления административных привилегий.
     */
    public void makeUserAdmin() {
        this.userRole = UserRole.ADMIN;
    }
}
