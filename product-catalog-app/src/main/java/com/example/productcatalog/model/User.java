package com.example.productcatalog.model;

import com.example.productcatalog.model.enums.UserRole;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая пользователя в системе.
 * <p>
 * Содержит учетные данные пользователя и информацию о ролях для аутентификации и авторизации.
 * Поддерживает сериализацию для кеширования и управления сеансами.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Уникальное имя пользователя для аутентификации.
     */
    private String username;

    /**
     * Хеш пароля для безопасной аутентификации.
     */
    private String passwordHash;

    /**
     * Роль пользователя, определяющая права доступа.
     */
    private UserRole userRole;

    /**
     * Временная метка создания пользователя.
     */
    private LocalDateTime createdAt;

    /**
     * Создает нового пользователя с ролью USER по умолчанию.
     *
     * @param username     имя пользователя для аутентификации
     * @param passwordHash хеш пароля
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.userRole = UserRole.USER;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Повышает пользователя до роли ADMIN.
     */
    public void makeUserAdmin() {
        this.userRole = UserRole.ADMIN;
    }
}
