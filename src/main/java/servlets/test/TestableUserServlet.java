package servlets.test;

import servlets.UserServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestableUserServlet extends UserServlet {
    public void doGetPublic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        super.doGet(req, resp);
    }
}
