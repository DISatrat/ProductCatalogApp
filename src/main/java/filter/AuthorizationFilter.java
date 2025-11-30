package filter;

import model.enums.UserRole;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class AuthorizationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    private static final Map<String, Set<UserRole>> ENDPOINT_ROLES = Map.of(
            "/api/users", Set.of(UserRole.ADMIN),
            "/api/audit-logs", Set.of(UserRole.ADMIN),
            "/api/products", Set.of(UserRole.USER, UserRole.ADMIN),
            "/api/metrics", Set.of(UserRole.USER, UserRole.ADMIN)
    );

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        String userRoleHeader = httpRequest.getHeader("X-User-Role");

        if (userRoleHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(userRoleHeader);
        } catch (IllegalArgumentException e) {
            logger.warning("Authorization failed: invalid role - " + userRoleHeader);
            sendForbiddenResponse(httpResponse, "Invalid user role");
            return;
        }

        if (!hasAccess(path, userRole)) {
            logger.warning("Authorization failed: user with role " + userRole + " denied access to " + path);
            sendForbiddenResponse(httpResponse, "Access denied. Insufficient permissions.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean hasAccess(String path, UserRole userRole) {
        for (Map.Entry<String, Set<UserRole>> entry : ENDPOINT_ROLES.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue().contains(userRole);
            }
        }
        return true;
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
    }
}
