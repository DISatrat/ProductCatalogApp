import aspects.AuditAspect;
import aspects.PerformanceLoggingAspect;
import config.Config;
import config.DatabaseMigrator;
import filter.AuthenticationFilter;
import filter.AuthorizationFilter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import listener.ApplicationContextListener;
import servlets.*;
import util.ApplicationContext;
import util.ConnectionPoolManager;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductCatalogApp {

    private static final Logger logger = Logger.getLogger(ProductCatalogApp.class.getName());

    private static final String CONFIG_FILE = "dev.yaml";
    private static final String CONTEXT_PATH = "";
    private static final String SERVLET_AUTH = "authServlet";
    private static final String SERVLET_PRODUCT = "productServlet";
    private static final String SERVLET_METRICS = "metricsServlet";
    private static final String SERVLET_USER = "userServlet";
    private static final String SERVLET_AUDIT = "auditServlet";

    public static void main(String[] args) {
        try {
            Config config = new Config(CONFIG_FILE);

            ConnectionPoolManager.initialize(config);

            DatabaseMigrator migrator = new DatabaseMigrator();
            migrator.runMigrations(config);

            ApplicationContext.initialize();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                ApplicationContext.shutdown();
                ConnectionPoolManager.close();
            }));

            forceLoadAspects();
            initAspects();
            startEmbeddedServer(config);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Application startup failed: " + e.getMessage(), e);
            ConnectionPoolManager.close();
            System.exit(1);
        }
    }

    private static void startEmbeddedServer(Config config) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(config.getServerPort());
        tomcat.setBaseDir(createTempDir());

        Context context = tomcat.addContext(CONTEXT_PATH, new File(".").getAbsolutePath());

        addFilters(context);

        Tomcat.addServlet(context, SERVLET_AUTH, new AuthServlet());
        Tomcat.addServlet(context, SERVLET_PRODUCT, new ProductServlet());
        Tomcat.addServlet(context, SERVLET_METRICS, new MetricsServlet());
        Tomcat.addServlet(context, SERVLET_USER, new UserServlet());
        Tomcat.addServlet(context, SERVLET_AUDIT, new AuditServlet());

        context.addServletMappingDecoded("/api/auth/*", SERVLET_AUTH);
        context.addServletMappingDecoded("/api/products/*", SERVLET_PRODUCT);
        context.addServletMappingDecoded("/api/metrics/*", SERVLET_METRICS);
        context.addServletMappingDecoded("/api/users", SERVLET_USER);
        context.addServletMappingDecoded("/api/audit-logs", SERVLET_AUDIT);

        logger.info("Starting embedded Tomcat...");
        tomcat.getConnector();
        tomcat.start();
        logger.info("Tomcat started on port " + config.getServerPort());
        tomcat.getServer().await();
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

    private static String createTempDir() {
        String tempDir = System.getProperty("java.io.tmpdir") + "/tomcat-" + System.currentTimeMillis();
        new File(tempDir).mkdirs();
        return tempDir;
    }

    private static void forceLoadAspects() {
        try {
            Class.forName("aspects.AuditAspect");
            Class.forName("aspects.PerformanceLoggingAspect");
            logger.info("Aspects loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.warning("Failed to load aspects: " + e.getMessage());
        }
    }

    private static void initAspects() {
        try {
            AuditAspect auditAspect = new AuditAspect();
            PerformanceLoggingAspect performanceAspect = new PerformanceLoggingAspect();

            logger.info("Aspects initialized: AuditAspect=" + auditAspect + ", PerformanceAspect=" + performanceAspect);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize aspects: " + e.getMessage(), e);
        }
    }
}