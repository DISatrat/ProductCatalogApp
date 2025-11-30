package servlets;

import config.BaseServlet;
import controller.AuditController;
import dto.AuditEntryDTO;
import util.ApplicationContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/audit-logs")
public class AuditServlet extends BaseServlet {
    private final AuditController auditController;

    public AuditServlet() {
        this.auditController = new AuditController(ApplicationContext.getAuditService());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String countParam = request.getParameter("limit");
            int count = countParam != null ? Integer.parseInt(countParam) : 50;

            List<AuditEntryDTO> auditLogs = auditController.getRecentAuditEntries(count);

            Map<String, Object> result = Map.of(
                    "success", true,
                    "data", auditLogs,
                    "total", auditLogs.size()
            );

            sendJsonResponse(response, result, HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}