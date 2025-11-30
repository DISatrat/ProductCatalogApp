package test.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditServletIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAuditLogs_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        HttpResponse response = get("/api/audit-logs", "1", "regularuser", "USER");

        assertEquals(403, response.statusCode());
        assertTrue(response.containsText("\"success\":false"));
    }

    @Test
    void getAuditLogs_ShouldReturnLogs_WhenAdmin() throws Exception {
        HttpResponse response = get("/api/audit-logs", "1", "adminuser", "ADMIN");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"data\""));
    }

    @Test
    void getAuditLogs_ShouldReturnUnauthorized_WhenNoHeaders() throws Exception {
        HttpResponse response = get("/api/audit-logs");

        assertEquals(401, response.statusCode());
    }

    @Test
    void getAuditLogs_ShouldSupportLimitParameter() throws Exception {
        HttpResponse response = get("/api/audit-logs?limit=10", "1", "adminuser", "ADMIN");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
    }
}
