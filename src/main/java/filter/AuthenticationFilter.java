package filter;

import service.user.UserService;
import util.ApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class AuthenticationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) {
        this.userService = ApplicationContext.getUserService();
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

        String userId = httpRequest.getHeader("X-User-Id");
        String username = httpRequest.getHeader("X-Username");

        if (userId == null || username == null) {
            logger.warning("Authentication failed: missing headers for path " + path);
            sendUnauthorizedResponse(httpResponse, "Authentication required");
            return;
        }

        try {
            Long.parseLong(userId);
        } catch (NumberFormatException e) {
            logger.warning("Authentication failed: invalid user ID format");
            sendUnauthorizedResponse(httpResponse, "Invalid user ID format");
            return;
        }

        if (userService != null && !userService.existsByUsername(username)) {
            logger.warning("Authentication failed: user not found - " + username);
            sendUnauthorizedResponse(httpResponse, "User not found");
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

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
    }
}
