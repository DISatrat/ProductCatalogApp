package test.main.repository;

import main.model.User;
import main.repository.UserRepository;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class UserRepositoryTest extends TestBase {

    private UserRepository userRepository;
    private User testUser;

    public static void main(String[] args) {
        UserRepositoryTest test = new UserRepositoryTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== Running UserRepository Tests ===");

        testFindByUsernameWhenUserExists();
        testFindByUsernameWhenUserNotExists();
        testFindByUsernameWhenUsernameIsNull();
        testAddUserWhenNewUser();
        testAddUserWhenDuplicateUser();
        testAddUserWhenUserIsNull();
        testFindAllWhenNoUsers();
        testFindAllWhenUsersExist();
        testIntegrationMultipleOperations();

        System.out.println("=== All UserRepository tests passed! ===");
    }

    private void setUp() {
        userRepository = new UserRepository();
        testUser = new User("testuser", "password123");
    }

    public void testFindByUsernameWhenUserExists() {
        setUp();
        userRepository.addUser(testUser);

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("testuser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testFindByUsernameWhenUserExists passed");
    }

    public void testFindByUsernameWhenUserNotExists() {
        setUp();

        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        assertFalse(foundUser.isPresent(), "User should not be found");
        System.out.println("✓ testFindByUsernameWhenUserNotExists passed");
    }

    public void testFindByUsernameWhenUsernameIsNull() {
        setUp();

        assertThrows(NullPointerException.class, () -> {
            userRepository.findByUsername(null);
        }, "Should throw NullPointerException for null username");
        System.out.println("✓ testFindByUsernameWhenUsernameIsNull passed");
    }

    public void testAddUserWhenNewUser() {
        setUp();

        userRepository.addUser(testUser);
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should be added");
        assertEquals("testuser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testAddUserWhenNewUser passed");
    }

    public void testAddUserWhenDuplicateUser() {
        setUp();
        userRepository.addUser(testUser);
        User duplicateUser = new User("testuser", "123");

        userRepository.addUser(duplicateUser);
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should exist");
        assertEquals("Test", foundUser.get().getUsername(), "First name should not change");
        assertEquals("password123", foundUser.get().getPasswordHash(), "Password should not change");
        System.out.println("✓ testAddUserWhenDuplicateUser passed");
    }

    public void testAddUserWhenUserIsNull() {
        setUp();

        assertThrows(NullPointerException.class, () -> {
            userRepository.addUser(null);
        }, "Should throw NullPointerException for null user");
        System.out.println("✓ testAddUserWhenUserIsNull passed");
    }

    public void testFindAllWhenNoUsers() {
        setUp();

        List<User> users = userRepository.findAll();

        assertNotNull(users, "List should not be null");
        assertTrue(users.isEmpty(), "List should be empty");
        System.out.println("✓ testFindAllWhenNoUsers passed");
    }

    public void testFindAllWhenUsersExist() {
        setUp();
        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");

        userRepository.addUser(user1);
        userRepository.addUser(user2);
        List<User> users = userRepository.findAll();

        assertNotNull(users, "List should not be null");
        assertEquals(2, users.size(), "Should have 2 users");

        boolean hasUser1 = users.stream().anyMatch(u -> u.getUsername().equals("user1"));
        boolean hasUser2 = users.stream().anyMatch(u -> u.getUsername().equals("user2"));

        assertTrue(hasUser1, "Should contain user1");
        assertTrue(hasUser2, "Should contain user2");
        System.out.println("✓ testFindAllWhenUsersExist passed");
    }

    public void testIntegrationMultipleOperations() {
        setUp();
        User user1 = new User("john", "password");
        User user2 = new User("jane", "secret");

        userRepository.addUser(user1);
        userRepository.addUser(user2);

        assertEquals(2, userRepository.findAll().size(), "Should have 2 users");
        assertTrue(userRepository.findByUsername("john").isPresent(), "John should exist");
        assertTrue(userRepository.findByUsername("jane").isPresent(), "Jane should exist");
        assertFalse(userRepository.findByUsername("bob").isPresent(), "Bob should not exist");
        System.out.println("✓ testIntegrationMultipleOperations passed");
    }
}