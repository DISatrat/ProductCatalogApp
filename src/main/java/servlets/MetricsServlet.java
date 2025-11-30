package servlets;

import config.BaseServlet;
import controller.MetricsController;
import dto.MetricsResponseDTO;
import mapper.MetricsMapper;
import util.ApplicationContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/metrics/*")
public class MetricsServlet extends BaseServlet {
    private final MetricsController metricsController;
    private final MetricsMapper metricsMapper;

    public MetricsServlet() {
        this.metricsController = new MetricsController(ApplicationContext.getMetricsService());
        this.metricsMapper = MetricsMapper.INSTANCE;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/metrics
                handleGetAllMetrics(request, response);
            } else if (pathInfo.equals("/search-count")) {
                // GET /api/metrics/search-count
                handleGetSearchCount(request, response);
            } else if (pathInfo.equals("/average-time")) {
                // GET /api/metrics/average-time
                handleGetAverageSearchTime(request, response);
            } else {
                sendError(response, "Not found", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetAllMetrics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long searchCount = metricsController.getSearchCount();
        double averageTime = metricsController.getAverageSearchTimeMs();

        MetricsResponseDTO metricsDTO = metricsMapper.toDTO(searchCount, averageTime);

        Map<String, Object> result = Map.of(
                "success", true,
                "metrics", metricsDTO
        );
        sendJsonResponse(response, result, HttpServletResponse.SC_OK);
    }

    private void handleGetSearchCount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long searchCount = metricsController.getSearchCount();

        Map<String, Object> result = Map.of(
                "success", true,
                "searchCount", searchCount
        );
        sendJsonResponse(response, result, HttpServletResponse.SC_OK);
    }

    private void handleGetAverageSearchTime(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        double averageTime = metricsController.getAverageSearchTimeMs();

        Map<String, Object> result = Map.of(
                "success", true,
                "averageSearchTimeMs", averageTime
        );
        sendJsonResponse(response, result, HttpServletResponse.SC_OK);
    }
}