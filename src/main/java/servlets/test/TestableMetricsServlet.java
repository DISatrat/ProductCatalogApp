package servlets.test;

import servlets.MetricsServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestableMetricsServlet extends MetricsServlet {
    public void doGetPublic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doGet(req, resp);
    }
}
