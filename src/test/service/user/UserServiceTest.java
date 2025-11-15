package test.service.user;


import main.model.User;
import main.repository.UserRepository;
import main.service.user.UserServiceImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class UserServiceTest extends TestBase {

    private UserRepository userRepository;
    private UserServiceImpl userService;
    private User testUser;

    public static void main(String[] args) {
        UserServiceTest test = new UserServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== Running UserServiceImpl Tests ===");

        testFindByUsernameWhenUserExists();
        testFindByUsernameWhenUserNotExists();
        testAddUserShouldAddUserToRepository();
        testGetUsersWhenNoUsers();
        testGetUsersWhenUsersExist();
        testHashPasswordShouldReturnConsistentHash();
        testHashPasswordWithDifferentPasswords();
        testCheckPasswordWhenCorrectPassword();
        testCheckPasswordWhenWrongPassword();
        testCheckPasswordWhenNullPassword();
        testIntegrationUserRegistrationAndAuthentication();

        System.out.println("=== All UserServiceImpl tests passed! ===");
    }

    private void setUp() {
        userRepository = new UserRepository();
        userService = new UserServiceImpl(userRepository);
        testUser = new User("testuser", "password123");
    }

    public void testFindByUsernameWhenUserExists() {
        setUp();
        userRepository.addUser(testUser);

        Optional<User> foundUser = userService.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("testuser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testFindByUsernameWhenUserExists passed");
    }

    public void testFindByUsernameWhenUserNotExists() {
        setUp();

        Optional<User> foundUser = userService.findByUsername("unknown");

        assertFalse(foundUser.isPresent(), "User should not be found");
        System.out.println("✓ testFindByUsernameWhenUserNotExists passed");
    }

    public void testAddUserShouldAddUserToRepository() {
        setUp();

        userService.addUser(testUser);
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should be added to repository");
        System.out.println("✓ testAddUserShouldAddUserToRepository passed");
    }

    public void testGetUsersWhenNoUsers() {
        setUp();

        List<User> users = userService.getUsers();

        assertNotNull(users, "List should not be null");
        assertTrue(users.isEmpty(), "List should be empty");
        System.out.println("✓ testGetUsersWhenNoUsers passed");
    }

    public void testGetUsersWhenUsersExist() {
        setUp();
        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");

        userService.addUser(user1);
        userService.addUser(user2);
        List<User> users = userService.getUsers();

        assertEquals(2, users.size(), "Should have 2 users");
        System.out.println("✓ testGetUsersWhenUsersExist passed");
    }

    public void testHashPasswordShouldReturnConsistentHash() {
        setUp();
        String password = "mySecretPassword";

        String hash1 = userService.hashPassword(password);
        String hash2 = userService.hashPassword(password);

        assertNotNull(hash1, "Hash should not be null");
        assertEquals(hash1, hash2, "Hashes should be consistent");
        System.out.println("✓ testHashPasswordShouldReturnConsistentHash passed");
    }

    public void testHashPasswordWithDifferentPasswords() {
        setUp();

        String hash1 = userService.hashPassword("password1");
        String hash2 = userService.hashPassword("password2");

        assertNotNull(hash1, "Hash1 should not be null");
        assertNotNull(hash2, "Hash2 should not be null");
        assertFalse(hash1.equals(hash2), "Hashes should be different for different passwords");
        System.out.println("✓ testHashPasswordWithDifferentPasswords passed");
    }

    public void testCheckPasswordWhenCorrectPassword() {
        setUp();
        String password = "correctPassword";
        String hash = userService.hashPassword(password);

        boolean result = userService.checkPassword(password, hash);

        assertTrue(result, "Password check should pass for correct password");
        System.out.println("✓ testCheckPasswordWhenCorrectPassword passed");
    }

    public void testCheckPasswordWhenWrongPassword() {
        setUp();
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hash = userService.hashPassword(correctPassword);

        boolean result = userService.checkPassword(wrongPassword, hash);

        assertFalse(result, "Password check should fail for wrong password");
        System.out.println("✓ testCheckPasswordWhenWrongPassword passed");
    }

    public void testCheckPasswordWhenNullPassword() {
        setUp();
        String hash = userService.hashPassword("somePassword");

        boolean result = userService.checkPassword(null, hash);

        assertFalse(result, "Password check should fail for null password");
        System.out.println("✓ testCheckPasswordWhenNullPassword passed");
    }

    public void testIntegrationUserRegistrationAndAuthentication() {
        setUp();
        String username = "alice";
        String password = "securePassword";
        String hashedPassword = userService.hashPassword(password);

        User newUser = new User(username, hashedPassword);

        // Регистрация
        userService.addUser(newUser);

        // Аутентификация
        Optional<User> foundUser = userService.findByUsername(username);
        boolean passwordValid = foundUser
                .map(user -> userService.checkPassword(password, user.getPasswordHash()))
                .orElse(false);

        assertTrue(foundUser.isPresent(), "User should be found");
        assertTrue(passwordValid, "Password should be valid");
        assertEquals("Alice", foundUser.get().getUsername(), "First name should match");
        System.out.println("✓ testIntegrationUserRegistrationAndAuthentication passed");
    }
}
