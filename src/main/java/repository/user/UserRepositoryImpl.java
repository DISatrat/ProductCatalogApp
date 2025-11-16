package repository.user;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория пользователей
 */
public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            throw new NullPointerException("Username cannot be null");
        }

        String sql = "SELECT id, username, password_hash, user_role FROM app_schema.users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password_hash"));
                setUserId(user, rs.getLong("id"));
                setUserRole(user, rs.getString("user_role"));
                return Optional.of(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding user", e);
        }
    }

    @Override
    public void addUser(User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }

        String sql = "INSERT INTO app_schema.users (username, password_hash, user_role) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getUserRole().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        setUserId(user, generatedKeys.getLong(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while adding user", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, user_role FROM app_schema.users";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password_hash"));
                setUserId(user, rs.getLong("id"));
                setUserRole(user, rs.getString("user_role"));
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding all users", e);
        }

        return users;
    }

    private void setUserId(User user, Long id) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set user ID", e);
        }
    }

    private void setUserRole(User user, String role) {
        try {
            if ("ADMIN".equals(role)) {
                user.makeUserAdmin();
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot set user role", e);
        }
    }
}