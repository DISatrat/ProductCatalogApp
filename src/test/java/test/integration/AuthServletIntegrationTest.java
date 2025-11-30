package test.integration;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServletIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_USER = "integrationTestUser";
    private static final String TEST_PASSWORD = "testPassword123";

    @Test
    @Order(1)
    void register_ShouldCreateNewUser() throws Exception {
        String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                TEST_USER, TEST_PASSWORD
        );

        HttpResponse response = post("/api/auth/register", jsonBody);

        assertEquals(201, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("User registered successfully"));
        assertTrue(response.containsText(TEST_USER));
    }

    @Test
    @Order(2)
    void register_ShouldReturnConflict_WhenUserExists() throws Exception {
        String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                TEST_USER, TEST_PASSWORD
        );

        HttpResponse response = post("/api/auth/register", jsonBody);

        assertEquals(409, response.statusCode());
        assertTrue(response.containsText("\"success\":false"));
    }

    @Test
    @Order(3)
    void login_ShouldAuthenticateUser() throws Exception {
        String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}",
                TEST_USER, TEST_PASSWORD
        );

        HttpResponse response = post("/api/auth/login", jsonBody);

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("Login successful"));
    }

    @Test
    @Order(4)
    void login_ShouldReturnUnauthorized_WhenWrongPassword() throws Exception {
        String jsonBody = String.format(
                "{\"username\":\"%s\",\"password\":\"wrongPassword\"}",
                TEST_USER
        );

        HttpResponse response = post("/api/auth/login", jsonBody);

        assertEquals(401, response.statusCode());
        assertTrue(response.containsText("\"success\":false"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenMissingUsername() throws Exception {
        String jsonBody = "{\"password\":\"password123\"}";

        HttpResponse response = post("/api/auth/login", jsonBody);

        assertEquals(400, response.statusCode());
        assertTrue(response.containsText("Username is required"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenMissingPassword() throws Exception {
        String jsonBody = "{\"username\":\"testuser\"}";

        HttpResponse response = post("/api/auth/login", jsonBody);

        assertEquals(400, response.statusCode());
        assertTrue(response.containsText("Password is required"));
    }

    @Test
    void auth_ShouldReturnBadRequest_WhenNoAction() throws Exception {
        HttpResponse response = post("/api/auth/", "{}");

        assertEquals(400, response.statusCode());
        assertTrue(response.containsText("Action required"));
    }

    @Test
    void auth_ShouldReturnNotFound_WhenUnknownAction() throws Exception {
        HttpResponse response = post("/api/auth/unknown", "{}");

        assertEquals(404, response.statusCode());
        assertTrue(response.containsText("Unknown action"));
    }

    @Test
    @Order(5)
    void logout_ShouldLogoutUser() throws Exception {
        HttpResponse response = post("/api/auth/logout?username=" + TEST_USER, "");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("Logout successful"));
    }
}
