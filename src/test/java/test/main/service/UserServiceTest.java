package test.main.service;

import model.User;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import service.user.UserService;
import service.user.UserServiceImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class UserServiceTest extends TestBase {

    private UserService userService;
    private UserRepository userRepository;

    public static void main(String[] args) {
        try {
            UserServiceTest test = new UserServiceTest();
            test.runAllTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllTests() throws Exception {
        setUp();

        testFindByUsername();
        testAddUser();
        testGetUsers();
        testHashPassword();
        testCheckPassword();

        tearDown();
        System.out.println("All UserService tests passed!");
    }

    private void setUp() throws Exception {
        TestBase.setUpAll();
        userRepository = new UserRepositoryImpl(connection);
        userService = new UserServiceImpl(userRepository);
        clearUsers();
    }

    private void tearDown() throws Exception {
        TestBase.tearDownAll();
    }

    private void testFindByUsername() {
        System.out.println("Running testFindByUsername...");

        User testUser = new User("testuser_find", "password123");
        userService.addUser(testUser);

        Optional<User> found = userService.findByUsername("testuser_find");
        assertTrue(found.isPresent(), "Should find user by username");
        assertEquals("testuser_find", found.get().getUsername(), "Username should match");

        Optional<User> notFound = userService.findByUsername("nonexistent_user");
        assertFalse(notFound.isPresent(), "Should not find non-existent user");

        System.out.println("testFindByUsername PASSED");
    }

    private void testAddUser() {
        System.out.println("Running testAddUser...");

        User newUser = new User("new_test_user", "password456");
        userService.addUser(newUser);

        Optional<User> found = userService.findByUsername("new_test_user");
        assertTrue(found.isPresent(), "Added user should be findable");
        assertEquals("new_test_user", found.get().getUsername(), "Username should match");
        assertEquals("USER", found.get().getUserRole(), "Role should match");

        System.out.println("testAddUser PASSED");
    }

    private void testGetUsers() throws Exception {
        System.out.println("Running testGetUsers...");

        clearUsers();

        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");
        user2.makeUserAdmin();
        userService.addUser(user1);
        userService.addUser(user2);

        List<User> users = userService.getUsers();
        assertTrue(users.size() >= 2, "Should return all users");

        boolean foundUser1 = users.stream().anyMatch(u -> "user1".equals(u.getUsername()));
        boolean foundUser2 = users.stream().anyMatch(u -> "user2".equals(u.getUsername()));
        assertTrue(foundUser1, "Should contain user1");
        assertTrue(foundUser2, "Should contain user2");

        System.out.println("testGetUsers PASSED - found " + users.size() + " users");
    }

    private void testHashPassword() {
        System.out.println("Running testHashPassword...");

        String password = "testPassword123";
        String hash = userService.hashPassword(password);

        assertNotNull(hash, "Hash should not be null");
        assertFalse(hash.isEmpty(), "Hash should not be empty");

        String hash2 = userService.hashPassword(password);
        assertEquals(hash, hash2, "Same password should produce same hash");

        String differentPassword = "differentPassword";
        String differentHash = userService.hashPassword(differentPassword);
        assertFalse(hash.equals(differentHash), "Different passwords should produce different hashes");

        System.out.println("testHashPassword PASSED");
    }

    private void testCheckPassword() {
        System.out.println("Running testCheckPassword...");

        String password = "mySecretPassword";
        String correctHash = userService.hashPassword(password);
        String wrongPassword = "wrongPassword";

        boolean correct = userService.checkPassword(password, correctHash);
        assertTrue(correct, "Should return true for correct password");

        boolean wrong = userService.checkPassword(wrongPassword, correctHash);
        assertFalse(wrong, "Should return false for wrong password");

        String differentHash = userService.hashPassword("otherPassword");
        boolean different = userService.checkPassword(password, differentHash);
        assertFalse(different, "Should return false for incorrect hash");

        System.out.println("testCheckPassword PASSED");
    }

    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    protected void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("FAIL: " + message);
        }
    }
}
