package com.example.productcatalog.repository.user;

import com.example.productcatalog.exception.RepositoryException;
import com.example.productcatalog.model.User;
import com.example.productcatalog.model.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserRepository с использованием JDBC.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BY_USERNAME =
            "SELECT id, username, password_hash, user_role, created_at FROM app_schema.users WHERE username = ?";

    private static final String INSERT =
            "INSERT INTO app_schema.users (username, password_hash, user_role) VALUES (?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT id, username, password_hash, user_role, created_at FROM app_schema.users ORDER BY id";

    private static final String EXISTS_BY_USERNAME =
            "SELECT COUNT(*) FROM app_schema.users WHERE username = ?";

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString("username"))
            .passwordHash(rs.getString("password_hash"))
            .userRole(UserRole.valueOf(rs.getString("user_role")))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Поиск пользователя по имени: {}", username);
        try {
            List<User> users = jdbcTemplate.query(SELECT_BY_USERNAME, userRowMapper, username);
            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            log.error("Ошибка поиска пользователя по имени: {}", username, e);
            throw new RepositoryException("Ошибка поиска пользователя по имени: " + username, e);
        }
    }

    @Override
    public void save(User user) {
        log.debug("Сохранение пользователя: {}", user.getUsername());
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getUserRole().name());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                user.setId(keyHolder.getKey().longValue());
            }
            log.info("Пользователь успешно сохранен с идентификатором: {}", user.getId());
        } catch (Exception e) {
            log.error("Ошибка сохранения пользователя: {}", user.getUsername(), e);
            throw new RepositoryException("Ошибка сохранения пользователя: " + user.getUsername(), e);
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("Поиск всех пользователей");
        try {
            return jdbcTemplate.query(SELECT_ALL, userRowMapper);
        } catch (Exception e) {
            log.error("Ошибка поиска всех пользователей", e);
            throw new RepositoryException("Ошибка поиска всех пользователей", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Проверка существования пользователя: {}", username);
        try {
            Integer count = jdbcTemplate.queryForObject(EXISTS_BY_USERNAME, Integer.class, username);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Ошибка проверки существования пользователя: {}", username, e);
            throw new RepositoryException("Ошибка проверки существования пользователя: " + username, e);
        }
    }
}
