package test.main.service;
import model.AuditEntry;
import repository.audit.AuditRepository;
import repository.audit.AuditRepositoryImpl;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;
import test.TestBase;

import java.util.List;

public class AuditServiceTest extends TestBase {

    private AuditService auditService;
    private AuditRepository auditRepository;

    public static void main(String[] args) {
        try {
            AuditServiceTest test = new AuditServiceTest();
            test.runAllTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllTests() throws Exception {
        setUp();

        testRecord();
        testRecordWithNullParameters();
        testGetEntries();

        tearDown();
        System.out.println("All AuditService tests passed!");
    }

    private void setUp() throws Exception {
        TestBase.setUpAll();
        auditRepository = new AuditRepositoryImpl(connection);
        auditService = new AuditServiceImpl(auditRepository);
        clearAuditEntries();
    }

    private void tearDown() throws Exception {
        TestBase.tearDownAll();
    }

    private void testRecord() {
        System.out.println("Running testRecord...");

        // Record some audit entries
        auditService.record("testuser", "LOGIN", "User logged into the system");
        auditService.record("admin", "CREATE_PRODUCT", "Created product: iPhone 15");
        auditService.record("user123", "SEARCH", "Searched for: laptops");

        // Verify entries were recorded by retrieving them
        List<AuditEntry> entries = auditService.getEntries();
        assertTrue(entries.size() >= 3, "Should have recorded audit entries");

        // Verify the content of entries
        boolean foundLogin = false;
        boolean foundCreateProduct = false;
        boolean foundSearch = false;

        for (AuditEntry entry : entries) {
            if ("testuser".equals(entry.getUsername()) && "LOGIN".equals(entry.getAction())) {
                foundLogin = true;
            }
            if ("admin".equals(entry.getUsername()) && "CREATE_PRODUCT".equals(entry.getAction())) {
                foundCreateProduct = true;
            }
            if ("user123".equals(entry.getUsername()) && "SEARCH".equals(entry.getAction())) {
                foundSearch = true;
            }
        }

        assertTrue(foundLogin, "Should find LOGIN audit entry");
        assertTrue(foundCreateProduct, "Should find CREATE_PRODUCT audit entry");
        assertTrue(foundSearch, "Should find SEARCH audit entry");

        System.out.println("testRecord PASSED - recorded " + entries.size() + " audit entries");
    }

    private void testRecordWithNullParameters() {
        System.out.println("Running testRecordWithNullParameters...");

        try {
            auditService.record(null, "ACTION", "Details");
            throw new AssertionError("FAIL: Should throw exception for null username");
        } catch (NullPointerException e) {
            System.out.println("testRecordWithNullParameters FAILED - username is null");
        }

        try {
            auditService.record("user", null, "Details");
            throw new AssertionError("FAIL: Should throw exception for null action");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        try {
            auditService.record("user", "ACTION", null);
            throw new AssertionError("FAIL: Should throw exception for null details");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("testRecordWithNullParameters PASSED");
    }

    private void testGetEntries() {
        System.out.println("Running testGetEntries...");

        clearAuditEntries();

        List<AuditEntry> initialEntries = auditService.getEntries();
        assertNotNull(initialEntries, "getEntries should not return null");

        auditService.record("user1", "ACTION_1", "Test details 1");
        auditService.record("user2", "ACTION_2", "Test details 2");

        List<AuditEntry> entriesAfter = auditService.getEntries();
        assertNotNull(entriesAfter, "getEntries should not return null after recording");
        assertTrue(entriesAfter.size() >= initialEntries.size() + 2,
                "Should have more entries after recording new ones");

        boolean foundAction1 = false;
        boolean foundAction2 = false;

        for (AuditEntry entry : entriesAfter) {
            if ("ACTION_1".equals(entry.getAction()) && "user1".equals(entry.getUsername())) {
                foundAction1 = true;
            }
            if ("ACTION_2".equals(entry.getAction()) && "user2".equals(entry.getUsername())) {
                foundAction2 = true;
            }
        }

        assertTrue(foundAction1, "Should find ACTION_1 entry");
        assertTrue(foundAction2, "Should find ACTION_2 entry");

        System.out.println("testGetEntries PASSED - initial: " + initialEntries.size() +
                " entries, after: " + entriesAfter.size() + " entries");
    }

    private void clearAuditEntries() {
        try {
            var stmt = connection.createStatement();
            stmt.execute("DELETE FROM app_schema.audit_entries");
        } catch (Exception e) {
            System.out.println("Note: Could not clear audit entries: " + e.getMessage());
        }
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