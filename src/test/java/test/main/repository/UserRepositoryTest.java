package test.main.repository;

import model.User;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class UserRepositoryTest extends TestBase {

    private UserRepository userRepository;

    public static void main(String[] args) {
        UserRepositoryTest test = new UserRepositoryTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== Running UserRepository Tests ===");

        try {
            TestBase.setUpAll();

            testFindByUsernameWhenUserExists();
            testFindByUsernameWhenUserNotExists();
            testFindByUsernameWhenUsernameIsNull();
            testAddUserWhenNewUser();
            testAddUserWhenDuplicateUser();
            testAddUserWhenUserIsNull();
            testFindAllWhenNoUsers();
            testFindAllWhenUsersExist();
            testIntegrationMultipleOperations();
            testUserPersistence();

            TestBase.tearDownAll();

            System.out.println("=== All UserRepository tests passed! ===");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setUp() throws Exception {
        userRepository = new UserRepositoryImpl(connection);
        clearUsers();
    }

    public void testFindByUsernameWhenUserExists() throws Exception {
        setUp();
        User testUser = new User("existinguser", "password123");
        userRepository.addUser(testUser);

        Optional<User> foundUser = userRepository.findByUsername("existinguser");

        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("existinguser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testFindByUsernameWhenUserExists passed");
    }

    public void testFindByUsernameWhenUserNotExists() throws Exception {
        setUp();
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        assertFalse(foundUser.isPresent(), "User should not be found");
        System.out.println("✓ testFindByUsernameWhenUserNotExists passed");
    }

    public void testFindByUsernameWhenUsernameIsNull() throws Exception {
        setUp();
        try {
            userRepository.findByUsername(null);
            throw new AssertionError("Should throw NullPointerException for null username");
        } catch (NullPointerException e) {
            // Expected
        }
        System.out.println("✓ testFindByUsernameWhenUsernameIsNull passed");
    }

    public void testAddUserWhenNewUser() throws Exception {
        setUp();
        User testUser = new User("newuser", "password123");

        userRepository.addUser(testUser);
        Optional<User> foundUser = userRepository.findByUsername("newuser");

        assertTrue(foundUser.isPresent(), "User should be added");
        assertEquals("newuser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testAddUserWhenNewUser passed");
    }

    public void testAddUserWhenDuplicateUser() throws Exception {
        setUp();
        User firstUser = new User("duplicateuser", "password123");
        User duplicateUser = new User("duplicateuser", "differentpassword");

        userRepository.addUser(firstUser);
        userRepository.addUser(duplicateUser);

        Optional<User> foundUser = userRepository.findByUsername("duplicateuser");

        assertTrue(foundUser.isPresent(), "User should exist");
        assertEquals("duplicateuser", foundUser.get().getUsername(), "Username should match");
        assertEquals("password123", foundUser.get().getPasswordHash(), "Password should not change");
        System.out.println("✓ testAddUserWhenDuplicateUser passed");
    }

    public void testAddUserWhenUserIsNull() throws Exception {
        setUp();
        try {
            userRepository.addUser(null);
            throw new AssertionError("Should throw NullPointerException for null user");
        } catch (NullPointerException e) {
            // Expected
        }
        System.out.println("✓ testAddUserWhenUserIsNull passed");
    }

    public void testFindAllWhenNoUsers() throws Exception {
        setUp();
        clearUsers();

        List<User> users = userRepository.findAll();

        assertNotNull(users, "List should not be null");
        long nonTestUsers = users.stream()
                .filter(u -> !u.getUsername().equals("testuser"))
                .count();
        assertEquals(0, nonTestUsers, "Should have no additional users");
        System.out.println("✓ testFindAllWhenNoUsers passed");
    }

    public void testFindAllWhenUsersExist() throws Exception {
        setUp();
        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");

        userRepository.addUser(user1);
        userRepository.addUser(user2);
        List<User> users = userRepository.findAll();

        assertNotNull(users, "List should not be null");

        boolean hasUser1 = users.stream().anyMatch(u -> u.getUsername().equals("user1"));
        boolean hasUser2 = users.stream().anyMatch(u -> u.getUsername().equals("user2"));

        assertTrue(hasUser1, "Should contain user1");
        assertTrue(hasUser2, "Should contain user2");
        System.out.println("✓ testFindAllWhenUsersExist passed");
    }

    public void testIntegrationMultipleOperations() throws Exception {
        setUp();
        User user1 = new User("john", "password");
        User user2 = new User("jane", "secret");

        userRepository.addUser(user1);
        userRepository.addUser(user2);

        List<User> allUsers = userRepository.findAll();
        long testUsersCount = allUsers.stream()
                .filter(u -> u.getUsername().equals("john") || u.getUsername().equals("jane"))
                .count();

        assertEquals(2, testUsersCount, "Should have 2 test users");
        assertTrue(userRepository.findByUsername("john").isPresent(), "John should exist");
        assertTrue(userRepository.findByUsername("jane").isPresent(), "Jane should exist");
        assertFalse(userRepository.findByUsername("bob").isPresent(), "Bob should not exist");
        System.out.println("✓ testIntegrationMultipleOperations passed");
    }

    public void testUserPersistence() throws Exception {
        setUp();
        User user = new User("persistentuser", "password");
        userRepository.addUser(user);

        UserRepository newRepository = new UserRepositoryImpl(connection);

        Optional<User> foundUser = newRepository.findByUsername("persistentuser");

        assertTrue(foundUser.isPresent(), "User should persist after repository recreation");
        assertEquals("persistentuser", foundUser.get().getUsername(), "Username should match");
        System.out.println("✓ testUserPersistence passed");
    }
}