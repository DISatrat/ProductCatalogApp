package test.main.servlet;

import controller.UserController;
import dto.UserResponseDTO;
import model.User;
import model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import servlets.UserServlet;
import servlets.test.TestableUserServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserController userController;

    private TestableUserServlet userServlet;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userServlet = new TestableUserServlet();

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        injectController();
    }

    @Test
    void doGet_ShouldReturnUsers_WhenAuthenticated() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-Username")).thenReturn("admin");
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN");

        User currentUser = User.builder()
                .id(1L)
                .username("admin")
                .userRole(UserRole.ADMIN)
                .build();

        List<UserResponseDTO> mockUsers = Arrays.asList(
                UserResponseDTO.builder().id(1L).username("admin").build(),
                UserResponseDTO.builder().id(2L).username("user1").build()
        );
    when(userController.getUsers(currentUser)).thenReturn(mockUsers);

        userServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"count\":2"));
        assertTrue(responseContent.contains("admin"));
        assertTrue(responseContent.contains("user1"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldReturnUnauthorized_WhenNoAuthHeaders() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-Username")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        userServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Authentication required"));
    }

    @Test
    void doGet_ShouldReturnForbidden_WhenSecurityException() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("2");
        when(request.getHeader("X-Username")).thenReturn("user");
        when(request.getHeader("X-User-Role")).thenReturn("USER");

        User currentUser = User.builder()
                .id(2L)
                .username("user")
                .userRole(UserRole.USER)
                .build();

        when(userController.getUsers(currentUser))
                .thenThrow(new SecurityException("Access denied"));

        userServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Access denied"));
    }

    private void injectController() {
        try {
            var controllerField = UserServlet.class.getDeclaredField("userController");
            controllerField.setAccessible(true);
            controllerField.set(userServlet, userController);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject controller", e);
        }
    }
}