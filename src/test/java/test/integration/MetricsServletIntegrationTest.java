package test.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsServletIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAllMetrics_ShouldReturnMetrics() throws Exception {
        HttpResponse response = get("/api/metrics/", "1", "testuser", "USER");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"metrics\""));
    }

    @Test
    void getSearchCount_ShouldReturnSearchCount() throws Exception {
        HttpResponse response = get("/api/metrics/search-count", "1", "testuser", "USER");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"searchCount\""));
    }

    @Test
    void getAverageTime_ShouldReturnAverageTime() throws Exception {
        HttpResponse response = get("/api/metrics/average-time", "1", "testuser", "USER");

        assertEquals(200, response.statusCode());
        assertTrue(response.containsText("\"success\":true"));
        assertTrue(response.containsText("\"averageSearchTimeMs\""));
    }

    @Test
    void getMetrics_ShouldReturnNotFound_WhenInvalidPath() throws Exception {
        HttpResponse response = get("/api/metrics/invalid", "1", "testuser", "USER");

        assertEquals(404, response.statusCode());
    }
}
