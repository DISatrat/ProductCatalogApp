package test.main.repository;

import exception.UserRepositoryException;
import model.User;
import model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import util.SQLConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSet generatedKeys;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    void testFindByUsername_ExistingUser_ShouldReturnUser() throws SQLException {
        String username = "testuser";
        when(connection.prepareStatement(SQLConstants.User.SELECT_BY_USERNAME)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password_hash")).thenReturn("hashedPassword");
        when(resultSet.getString("user_role")).thenReturn("USER");

        Optional<User> result = userRepository.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals("hashedPassword", result.get().getPasswordHash());
        assertEquals(UserRole.USER, result.get().getUserRole());

        verify(preparedStatement).setString(1, username);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testFindByUsername_NonExistentUser_ShouldReturnEmpty() throws SQLException {
        String username = "nonexistent";
        when(connection.prepareStatement(SQLConstants.User.SELECT_BY_USERNAME)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<User> result = userRepository.findByUsername(username);

        assertFalse(result.isPresent());
        verify(preparedStatement).setString(1, username);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testFindByUsername_NullUsername_ShouldThrowException() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            userRepository.findByUsername(null);
        });

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void testAddUser_NewUser_ShouldInsertAndSetId() throws SQLException {
        User user = new User("newuser", "password123");
        when(connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getLong(1)).thenReturn(123L);

        userRepository.addUser(user);

        assertEquals(123L, user.getId());
        verify(preparedStatement).setString(1, "newuser");
        verify(preparedStatement).setString(2, "password123");
        verify(preparedStatement).setString(3, "USER");
        verify(preparedStatement).executeUpdate();
        verify(generatedKeys).next();
        verify(generatedKeys).getLong(1);
    }

    @Test
    void testAddUser_AdminUser_ShouldInsertWithAdminRole() throws SQLException {
        User user = new User("adminuser", "adminpass");
        user.makeUserAdmin();

        when(connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getLong(1)).thenReturn(123L);

        userRepository.addUser(user);

        verify(preparedStatement).setString(3, "ADMIN");
    }

    @Test
    void testAddUser_DuplicateUsername_ShouldThrowException() throws SQLException {
        User user = new User("duplicate", "password");
        when(connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate key", "23505"));

        assertThrows(UserRepositoryException.class, () -> {
            userRepository.addUser(user);
        });

        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testAddUser_NullUser_ShouldThrowException() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            userRepository.addUser(null);
        });

        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void testAddUser_NoGeneratedKey_ShouldThrowException() throws SQLException {
        User user = new User("newuser", "password");
        when(connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        assertThrows(UserRepositoryException.class, () -> {
            userRepository.addUser(user);
        });
    }

    @Test
    void testAddUser_NoRowsAffected_ShouldThrowException() throws SQLException {
        User user = new User("newuser", "password");
        when(connection.prepareStatement(SQLConstants.User.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(UserRepositoryException.class, () -> {
            userRepository.addUser(user);
        });
    }

    @Test
    void testFindAll_ShouldReturnAllUsers() throws SQLException {
        when(connection.prepareStatement(SQLConstants.User.SELECT_ALL)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);

        when(resultSet.getLong("id")).thenReturn(1L, 2L);
        when(resultSet.getString("username")).thenReturn("user1", "user2");
        when(resultSet.getString("password_hash")).thenReturn("hash1", "hash2");
        when(resultSet.getString("user_role")).thenReturn("USER", "ADMIN");

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());

        User firstUser = result.get(0);
        assertEquals("user1", firstUser.getUsername());
        assertEquals("hash1", firstUser.getPasswordHash());
        assertEquals(UserRole.USER, firstUser.getUserRole());

        User secondUser = result.get(1);
        assertEquals("user2", secondUser.getUsername());
        assertEquals("hash2", secondUser.getPasswordHash());
        assertEquals(UserRole.ADMIN, secondUser.getUserRole());

        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
    }

    @Test
    void testFindAll_EmptyResult_ShouldReturnEmptyList() throws SQLException {
        when(connection.prepareStatement(SQLConstants.User.SELECT_ALL)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        List<User> result = userRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testSQLExceptionInFindByUsername_ShouldWrapInRepositoryException() throws SQLException {
        String username = "testuser";
        when(connection.prepareStatement(SQLConstants.User.SELECT_BY_USERNAME))
                .thenThrow(new SQLException("Database error"));

        assertThrows(UserRepositoryException.class, () -> {
            userRepository.findByUsername(username);
        });
    }

    @Test
    void testSQLExceptionInFindAll_ShouldWrapInRepositoryException() throws SQLException {
        when(connection.prepareStatement(SQLConstants.User.SELECT_ALL))
                .thenThrow(new SQLException("Database error"));

        assertThrows(UserRepositoryException.class, () -> {
            userRepository.findAll();
        });
    }

    @Test
    void testSetUserRole_UnexpectedRole_ShouldHandleGracefully() throws SQLException {
        String username = "unknownroleuser";
        when(connection.prepareStatement(SQLConstants.User.SELECT_BY_USERNAME)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("username")).thenReturn(username);
        when(resultSet.getString("password_hash")).thenReturn("hash");
        when(resultSet.getString("user_role")).thenReturn("UNKNOWN_ROLE");

        Optional<User> result = userRepository.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(UserRole.USER, result.get().getUserRole());
    }
}