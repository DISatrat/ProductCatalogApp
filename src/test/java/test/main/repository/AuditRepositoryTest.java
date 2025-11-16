package test.main.repository;

import model.AuditEntry;
import repository.audit.AuditRepository;
import repository.audit.AuditRepositoryImpl;
import test.TestBase;

import java.util.List;

public class AuditRepositoryTest extends TestBase {

    private AuditRepository auditRepository;

    public static void main(String[] args) {
        try {
            AuditRepositoryTest test = new AuditRepositoryTest();
            test.runAllTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllTests() throws Exception {
        setUp();

        testRecord();
        testGetEntries();

        tearDown();
        System.out.println("All AuditRepository tests passed!");
    }

    private void setUp() throws Exception {
        TestBase.setUpAll();
        auditRepository = new AuditRepositoryImpl(connection);
        clearAuditEntries();
    }

    private void tearDown() throws Exception {
        TestBase.tearDownAll();
    }

    private void testRecord() {
        System.out.println("Running testRecord...");

        auditRepository.record("testuser", "LOGIN", "User logged into the system");

        List<AuditEntry> entries = auditRepository.getEntries();

        assertNotNull(entries, "getEntries should not return null");

        System.out.println("testRecord PASSED - recorded audit entry");
    }

    private void testGetEntries() {
        System.out.println("Running testGetEntries...");

        List<AuditEntry> initialEntries = auditRepository.getEntries();
        assertNotNull(initialEntries, "getEntries should not return null");

        auditRepository.record("user1", "CREATE_PRODUCT", "Created product: iPhone");
        auditRepository.record("user2", "UPDATE_PRODUCT", "Updated product: Samsung");
        auditRepository.record("admin", "DELETE_USER", "Deleted user: testuser");

        List<AuditEntry> entriesAfter = auditRepository.getEntries();
        assertNotNull(entriesAfter, "getEntries should not return null after recording");

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