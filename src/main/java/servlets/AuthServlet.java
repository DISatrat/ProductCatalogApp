package servlets;

import config.BaseServlet;
import controller.AuthController;
import dto.LoginRequestDTO;
import dto.RegisterRequestDTO;
import dto.UserResponseDTO;
import util.ServiceLocator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth")
public class AuthServlet extends BaseServlet {
    private final AuthController authController;

    public AuthServlet() {
        this.authController = new AuthController(
                ServiceLocator.getUserService(),
                ServiceLocator.getAuditService()
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                sendError(response, "Action required (login or register)", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            switch (pathInfo) {
                case "/login":
                    handleLogin(request, response);
                    break;
                case "/register":
                    handleRegister(request, response);
                    break;
                case "/logout":
                    handleLogout(request, response);
                    break;
                default:
                    sendError(response, "Unknown action: " + pathInfo, HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequestDTO loginRequest = parseJsonBody(request, LoginRequestDTO.class);

        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            sendError(response, "Username is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            sendError(response, "Password is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UserResponseDTO user = authController.login(loginRequest.getUsername(), loginRequest.getPassword());

            Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "Login successful",
                    "user", user
            );

            sendJsonResponse(response, result, HttpServletResponse.SC_OK);

        } catch (SecurityException e) {
            sendError(response, "Invalid username or password", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RegisterRequestDTO registerRequest = parseJsonBody(request, RegisterRequestDTO.class);

        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            sendError(response, "Username is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            sendError(response, "Password is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            UserResponseDTO user = authController.register(registerRequest.getUsername(), registerRequest.getPassword());

            Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "User registered successfully",
                    "user", user
            );

            sendJsonResponse(response, result, HttpServletResponse.SC_CREATED);

        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_CONFLICT);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            sendError(response, "Username is required for logout", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            authController.logout(username);

            Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "Logout successful"
            );

            sendJsonResponse(response, result, HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}