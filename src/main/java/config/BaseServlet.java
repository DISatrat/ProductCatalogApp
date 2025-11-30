package config;

import util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseServlet extends HttpServlet {
    protected void sendJsonResponse(HttpServletResponse response, Object data, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonUtil.writeJson(response, data);
    }

    protected void sendError(HttpServletResponse response, String message, int status) throws IOException {
        Map<String, String> error = Map.of("error", message);
        sendJsonResponse(response, error, status);
    }

    protected <T> T parseJsonBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        return JsonUtil.fromJson(body, clazz);
    }
}