package servlets;

import config.BaseServlet;
import controller.UserController;
import dto.UserResponseDTO;
import model.User;
import model.enums.UserRole;
import util.ServiceLocator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/users")
public class UserServlet extends BaseServlet {
    private final UserController userController;

    public UserServlet() {
        this.userController = new UserController(
                ServiceLocator.getUserService()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            User currentUser = getCurrentUserFromRequest(request);
            if (currentUser == null) {
                sendError(response, "Authentication required", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<UserResponseDTO> users = userController.getUsers(currentUser);

            Map<String, Object> result = Map.of(
                    "success", true,
                    "users", users,
                    "count", users.size()
            );
            sendJsonResponse(response, result, HttpServletResponse.SC_OK);

        } catch (SecurityException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        } catch (NullPointerException e) {
            sendError(response, "Current user information is missing", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendError(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private User getCurrentUserFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        String usernameHeader = request.getHeader("X-Username");
        String userRoleHeader = request.getHeader("X-User-Role");

        if (userIdHeader == null || usernameHeader == null || userRoleHeader == null) {
            return null;
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            UserRole userRole = UserRole.valueOf(userRoleHeader);

            return User.builder()
                    .id(userId)
                    .username(usernameHeader)
                    .userRole(userRole)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}