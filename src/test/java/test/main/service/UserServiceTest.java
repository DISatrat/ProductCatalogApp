package test.main.service;

import model.User;
import model.enums.UserRole;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import service.user.UserService;
import service.user.UserServiceImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testFindByUsername_Found() {
        String username = "testuser";
        User expectedUser = createUser(1L, username, "hashedPassword", UserRole.USER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_NotFound() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(username);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_NullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findByUsername(null);
        });
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void testAddUser() {
        User newUser = createUser(null, "newuser", "plainPassword", UserRole.USER);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        doNothing().when(userRepository).addUser(any(User.class));

        userService.addUser(newUser);

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).addUser(newUser);
    }

    @Test
    void testAddUser_AlreadyExists() {
        User existingUser = createUser(1L, "existinguser", "password", UserRole.USER);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalStateException.class, () -> {
            userService.addUser(existingUser);
        });
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void testAddUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(null);
        });
        verify(userRepository, never()).findByUsername(any());
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void testGetUsers() {
        List<User> expectedUsers = Arrays.asList(
                createUser(1L, "user1", "hash1", UserRole.USER),
                createUser(2L, "user2", "hash2", UserRole.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getUsers();

        assertEquals(expectedUsers, result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUsers_Empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testHashPassword() {
        String password = "testPassword123";

        String hash1 = userService.hashPassword(password);
        String hash2 = userService.hashPassword(password);

        assertNotNull(hash1);
        assertFalse(hash1.isEmpty());
        assertEquals(hash1, hash2, "Same password should produce same hash");
    }

    @Test
    void testHashPassword_NullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.hashPassword(null);
        });
    }

    @Test
    void testHashPassword_EmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.hashPassword("");
        });
    }

    @Test
    void testCheckPassword_Correct() {
        String password = "mySecretPassword";
        String hash = userService.hashPassword(password);

        boolean result = userService.checkPassword(password, hash);

        assertTrue(result);
    }

    @Test
    void testCheckPassword_WrongPassword() {
        String correctPassword = "mySecretPassword";
        String wrongPassword = "wrongPassword";
        String hash = userService.hashPassword(correctPassword);

        boolean result = userService.checkPassword(wrongPassword, hash);

        assertFalse(result);
    }

    @Test
    void testCheckPassword_WrongHash() {
        String password = "mySecretPassword";
        String wrongHash = userService.hashPassword("otherPassword");

        boolean result = userService.checkPassword(password, wrongHash);

        assertFalse(result);
    }

    @Test
    void testCheckPassword_NullPassword() {
        String hash = userService.hashPassword("password");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.checkPassword(null, hash);
        });
    }

    @Test
    void testCheckPassword_NullHash() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.checkPassword("password", null);
        });
    }

    @Test
    void testMakeUserAdmin() {
        User user = createUser(1L, "user", "password", UserRole.USER);

        user.makeUserAdmin();

        assertEquals(UserRole.ADMIN, user.getUserRole());
    }


    private User createUser(Long id, String username, String passwordHash, UserRole role) {
        User user = new User(username, passwordHash);
        user.setId(id);
        if (role == UserRole.ADMIN) {
            user.makeUserAdmin();
        }
        return user;
    }
}
