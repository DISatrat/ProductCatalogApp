package test.main.servlet;

import controller.AuthController;
import dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servlets.AuthServlet;
import servlets.test.TestableAuthServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AuthServletTest {

    private TestableAuthServlet authServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthController authController;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authController = mock(AuthController.class);
        authServlet = new TestableAuthServlet();

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        injectController();
    }

    @Test
    void doPost_ShouldHandleLoginSuccess() throws Exception {
        String jsonBody = "{\"username\":\"testuser\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);

        UserResponseDTO mockUser = UserResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .build();
        when(authController.login("testuser", "password123")).thenReturn(mockUser);

        authServlet.doPostPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("Login successful"));
        assertTrue(responseContent.contains("testuser"));

        verify(authController).login("testuser", "password123");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_ShouldHandleRegisterSuccess() throws Exception {
        String jsonBody = "{\"username\":\"newuser\",\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getReader()).thenReturn(reader);

        UserResponseDTO mockUser = UserResponseDTO.builder()
                .id(2L)
                .username("newuser")
                .build();
        when(authController.register("newuser", "password123")).thenReturn(mockUser);

        authServlet.doPostPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("User registered successfully"));
        assertTrue(responseContent.contains("newuser"));

        verify(authController).register("newuser", "password123");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void doPost_ShouldHandleLogoutSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");
        when(request.getParameter("username")).thenReturn("testuser");

        doNothing().when(authController).logout("testuser");

        authServlet.doPostPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("Logout successful"));

        verify(authController).logout("testuser");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenNoAction() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Action required"));
    }

    @Test
    void doPost_ShouldReturnNotFound_WhenUnknownAction() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Unknown action"));
    }

    @Test
    void doPost_ShouldReturnUnauthorized_WhenInvalidLogin() throws Exception {
        String jsonBody = "{\"username\":\"testuser\",\"password\":\"wrong\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);

        when(authController.login("testuser", "wrong"))
                .thenThrow(new SecurityException("Invalid credentials"));

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Invalid username or password"));
    }

    @Test
    void doPost_ShouldReturnConflict_WhenDuplicateRegistration() throws Exception {
        String jsonBody = "{\"username\":\"existing\",\"password\":\"password\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));

        when(request.getPathInfo()).thenReturn("/register");
        when(request.getReader()).thenReturn(reader);

        when(authController.register("existing", "password"))
                .thenThrow(new IllegalArgumentException("User already exists"));

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("User already exists"));
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenLoginMissingUsername() throws Exception {
        String jsonBody = "{\"password\":\"password123\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonBody));

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(reader);

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Username is required"));
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenLogoutMissingUsername() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");
        when(request.getParameter("username")).thenReturn(null);

        authServlet.doPostPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Username is required for logout"));
    }

    private void injectController() {
        try {
            var field = AuthServlet.class.getDeclaredField("authController");
            field.setAccessible(true);
            field.set(authServlet, authController);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject controller", e);
        }
    }
}