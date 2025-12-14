package com.example.productcatalog.service.user;

import com.example.productcatalog.model.User;
import com.example.productcatalog.model.enums.UserRole;
import com.example.productcatalog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Поиск пользователя по имени: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public User register(String username, String password) {
        log.info("Регистрация нового пользователя: {}", username);

        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь с именем '" + username + "' уже существует");
        }

        User user = User.builder()
                .username(username)
                .passwordHash(hashPassword(password))
                .userRole(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {}", username);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    @Override
    public boolean checkPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
