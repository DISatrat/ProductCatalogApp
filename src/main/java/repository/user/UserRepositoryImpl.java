package repository.user;

import exception.UserRepositoryException;
import model.User;
import util.ConnectionPoolManager;
import util.SQLConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория пользователей
 */
public class UserRepositoryImpl implements UserRepository {

    public UserRepositoryImpl() {
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.User.SELECT_BY_USERNAME)) {

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
            throw new UserRepositoryException("Database error while finding user by username: " + username, e);
        }
    }

    @Override
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getUserRole().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new UserRepositoryException("Failed to create user, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setUserId(user, generatedKeys.getLong(1));
                } else {
                    throw new UserRepositoryException("Failed to retrieve generated user ID");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new UserRepositoryException("User with username '" + user.getUsername() + "' already exists", e);
            }
            throw new UserRepositoryException("Database error while adding user: " + user.getUsername(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.User.SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getString("username"), rs.getString("password_hash"));
                setUserId(user, rs.getLong("id"));
                setUserRole(user, rs.getString("user_role"));
                users.add(user);
            }

        } catch (SQLException e) {
            throw new UserRepositoryException("Database error while retrieving all users", e);
        }

        return users;
    }

    private void setUserId(User user, Long id) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);

        } catch (NoSuchFieldException e) {
            throw new UserRepositoryException("User class does not have 'id' field", e);
        } catch (IllegalAccessException e) {
            throw new UserRepositoryException("Cannot access 'id' field in User class", e);
        }
    }

    private void setUserRole(User user, String role) {
        try {
            if ("ADMIN".equals(role)) {
                user.makeUserAdmin();
            }

        } catch (SecurityException e) {
            throw new UserRepositoryException("Security exception while setting user role: " + role, e);
        } catch (Exception e) {
            throw new UserRepositoryException("Unexpected error while setting user role: " + role, e);
        }
    }
}