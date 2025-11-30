package test.integration;

import filter.AuthenticationFilter;
import filter.AuthorizationFilter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import servlets.*;
import util.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

public abstract class BaseIntegrationTest {

    private static final Logger logger = Logger.getLogger(BaseIntegrationTest.class.getName());

    protected static Tomcat tomcat;
    protected static int port;
    protected static String baseUrl;
    protected static CloseableHttpClient httpClient;

    @BeforeAll
    static void setUpServer() throws Exception {
        port = findAvailablePort();
        baseUrl = "http://localhost:" + port;

        ApplicationContext.initialize();

        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(createTempDir());

        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        addFilters(context);

        Tomcat.addServlet(context, "authServlet", new AuthServlet());
        Tomcat.addServlet(context, "productServlet", new ProductServlet());
        Tomcat.addServlet(context, "metricsServlet", new MetricsServlet());
        Tomcat.addServlet(context, "userServlet", new UserServlet());
        Tomcat.addServlet(context, "auditServlet", new AuditServlet());

        context.addServletMappingDecoded("/api/auth/*", "authServlet");
        context.addServletMappingDecoded("/api/products/*", "productServlet");
        context.addServletMappingDecoded("/api/metrics/*", "metricsServlet");
        context.addServletMappingDecoded("/api/users", "userServlet");
        context.addServletMappingDecoded("/api/audit-logs", "auditServlet");

        tomcat.getConnector();
        tomcat.start();

        httpClient = HttpClients.createDefault();

        logger.info("Test server started on port " + port);
    }

    private static void addFilters(Context context) {
        FilterDef authFilterDef = new FilterDef();
        authFilterDef.setFilterName("authenticationFilter");
        authFilterDef.setFilterClass(AuthenticationFilter.class.getName());
        context.addFilterDef(authFilterDef);

        FilterMap authFilterMap = new FilterMap();
        authFilterMap.setFilterName("authenticationFilter");
        authFilterMap.addURLPattern("/api/*");
        context.addFilterMap(authFilterMap);

        FilterDef authzFilterDef = new FilterDef();
        authzFilterDef.setFilterName("authorizationFilter");
        authzFilterDef.setFilterClass(AuthorizationFilter.class.getName());
        context.addFilterDef(authzFilterDef);

        FilterMap authzFilterMap = new FilterMap();
        authzFilterMap.setFilterName("authorizationFilter");
        authzFilterMap.addURLPattern("/api/*");
        context.addFilterMap(authzFilterMap);
    }

    @AfterAll
    static void tearDownServer() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
        ApplicationContext.shutdown();
        logger.info("Test server stopped");
    }

    protected HttpResponse get(String path) throws IOException {
        HttpGet request = new HttpGet(baseUrl + path);
        return executeRequest(request);
    }

    protected HttpResponse get(String path, String userId, String username, String role) throws IOException {
        HttpGet request = new HttpGet(baseUrl + path);
        addAuthHeaders(request, userId, username, role);
        return executeRequest(request);
    }

    protected HttpResponse post(String path, String jsonBody) throws IOException {
        HttpPost request = new HttpPost(baseUrl + path);
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        return executeRequest(request);
    }

    protected HttpResponse post(String path, String jsonBody, String userId, String username, String role) throws IOException {
        HttpPost request = new HttpPost(baseUrl + path);
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        addAuthHeaders(request, userId, username, role);
        return executeRequest(request);
    }

    protected HttpResponse put(String path, String jsonBody, String userId, String username, String role) throws IOException {
        HttpPut request = new HttpPut(baseUrl + path);
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        addAuthHeaders(request, userId, username, role);
        return executeRequest(request);
    }

    protected HttpResponse delete(String path, String userId, String username, String role) throws IOException {
        HttpDelete request = new HttpDelete(baseUrl + path);
        addAuthHeaders(request, userId, username, role);
        return executeRequest(request);
    }

    private void addAuthHeaders(HttpUriRequestBase request, String userId, String username, String role) {
        if (userId != null) request.addHeader("X-User-Id", userId);
        if (username != null) request.addHeader("X-Username", username);
        if (role != null) request.addHeader("X-User-Role", role);
    }

    private HttpResponse executeRequest(HttpUriRequestBase request) throws IOException {
        return httpClient.execute(request, response -> {
            int statusCode = response.getCode();
            String body = EntityUtils.toString(response.getEntity());
            return new HttpResponse(statusCode, body);
        });
    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Could not find available port", e);
        }
    }

    private static String createTempDir() {
        String tempDir = System.getProperty("java.io.tmpdir") + "/tomcat-test-" + System.currentTimeMillis();
        new File(tempDir).mkdirs();
        return tempDir;
    }

    public record HttpResponse(int statusCode, String body) {
        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean containsText(String text) {
            return body != null && body.contains(text);
        }
    }
}
