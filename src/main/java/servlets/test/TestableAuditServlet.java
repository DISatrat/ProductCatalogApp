package servlets.test;

import servlets.AuditServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestableAuditServlet extends AuditServlet {
    public void doGetPublic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doGet(req, resp);
    }
}