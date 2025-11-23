package test.main.servlet;


import controller.MetricsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import servlets.MetricsServlet;
import servlets.test.TestableMetricsServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetricsServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private MetricsController metricsController;

    private TestableMetricsServlet metricsServlet;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        metricsServlet = new TestableMetricsServlet();

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        injectController();
    }

    @Test
    void doGet_ShouldReturnAllMetrics_WhenRootPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(metricsController.getSearchCount()).thenReturn(150L);
        when(metricsController.getAverageSearchTimeMs()).thenReturn(45.5);

        metricsServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"searchCount\":150"));
        assertTrue(responseContent.contains("\"averageSearchTimeMs\":45.5"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldReturnSearchCount_WhenSearchCountPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/search-count");
        when(metricsController.getSearchCount()).thenReturn(200L);

        metricsServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"searchCount\":200"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldReturnAverageTime_WhenAverageTimePath() throws Exception {
        when(request.getPathInfo()).thenReturn("/average-time");
        when(metricsController.getAverageSearchTimeMs()).thenReturn(32.1);

        metricsServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"averageSearchTimeMs\":32.1"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldReturnNotFound_WhenUnknownPath() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        metricsServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Not found"));
    }

    @Test
    void doGet_ShouldReturnInternalServerError_WhenControllerThrowsException() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(metricsController.getSearchCount()).thenThrow(new RuntimeException("DB error"));

        metricsServlet.doGetPublic(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        printWriter.flush();
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Internal server error"));
    }

    @Test
    void doGet_ShouldHandleNullPathInfo() throws Exception {
        when(request.getPathInfo()).thenReturn(null);
        when(metricsController.getSearchCount()).thenReturn(100L);
        when(metricsController.getAverageSearchTimeMs()).thenReturn(50.0);

        metricsServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"searchCount\":100"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGet_ShouldHandleEmptyPath() throws Exception {
        when(request.getPathInfo()).thenReturn("");
        when(metricsController.getSearchCount()).thenReturn(100L);
        when(metricsController.getAverageSearchTimeMs()).thenReturn(50.0);

        metricsServlet.doGetPublic(request, response);

        printWriter.flush();
        String responseContent = responseWriter.toString();

        assertTrue(responseContent.contains("\"success\":true"));
        assertTrue(responseContent.contains("\"searchCount\":100"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    private void injectController() {
        try {
            var controllerField = MetricsServlet.class.getDeclaredField("metricsController");
            controllerField.setAccessible(true);
            controllerField.set(metricsServlet, metricsController);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject controller", e);
        }
    }
}
