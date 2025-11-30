package test.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServletIntegrationTest extends BaseIntegrationTest {

    @Test
    void getUsers_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        HttpResponse response = get("/api/users", "1", "regularuser", "USER");

        assertEquals(403, response.statusCode());
        assertTrue(response.containsText("\"success\":false"));
    }

    @Test
    void getUsers_ShouldReturnUsers_WhenAdmin() throws Exception {
        HttpResponse response = get("/api/users", "1", "adminuser", "ADMIN");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"users\""));
    }

    @Test
    void getUsers_ShouldReturnUnauthorized_WhenNoHeaders() throws Exception {
        HttpResponse response = get("/api/users");

        assertEquals(401, response.statusCode());
    }
}
