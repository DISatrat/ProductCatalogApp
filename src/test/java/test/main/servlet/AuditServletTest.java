package test.main.servlet;

import controller.AuditController;
import dto.AuditEntryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import servlets.AuditServlet;
import servlets.test.TestableAuditServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuditServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuditController auditController;

    private TestableAuditServlet auditServlet;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        auditServlet = new TestableAuditServlet();

        setField(auditServlet, "auditController", auditController);

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doGet_ShouldReturnAuditLogs_WhenValidRequest() throws Exception {
        // Arrange
        List<AuditEntryDTO> mockLogs = Arrays.asList(
                AuditEntryDTO.builder()
                        .username("user1")
                        .action("LOGIN")
                        .details("User logged in")
                        .timestamp("2024-01-01 10:00:00")
                        .build(),
                AuditEntryDTO.builder()
                        .username("user2")
                        .action("CREATE_PRODUCT")
                        .details("Created product")
                        .timestamp("2024-01-01 10:01:00")
                        .build()
        );

        when(request.getParameter("limit")).thenReturn("10");
        when(auditController.getRecentAuditEntries(10)).thenReturn(mockLogs);

        auditServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"total\":2"));
        assertTrue(responseContent.contains("LOGIN"));
        assertTrue(responseContent.contains("CREATE_PRODUCT"));

        verify(auditController).getRecentAuditEntries(10);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldUseDefaultLimit_WhenNoLimitParameter() throws Exception {
        // Arrange
        List<AuditEntryDTO> mockLogs = Arrays.asList(
                AuditEntryDTO.builder()
                        .username("user1")
                        .action("LOGIN")
                        .details("User logged in")
                        .timestamp("2024-01-01 10:00:00")
                        .build()
        );

        when(request.getParameter("limit")).thenReturn(null);
        when(auditController.getRecentAuditEntries(50)).thenReturn(mockLogs);

        auditServlet.doGetPublic(request, response);

        verify(auditController).getRecentAuditEntries(50);
    }

    @Test
    void doGet_ShouldReturnBadRequest_WhenInvalidLimitParameter() throws Exception {
        when(request.getParameter("limit")).thenReturn("invalid");

        auditServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("\"success\":false"));
    }

    @Test
    void doGet_ShouldReturnInternalServerError_WhenControllerThrowsException() throws Exception {
        when(request.getParameter("limit")).thenReturn("10");
        when(auditController.getRecentAuditEntries(10)).thenThrow(new RuntimeException("Database error"));

        auditServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Internal server error"));
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
