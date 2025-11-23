import aspects.AuditAspect;
import aspects.PerformanceLoggingAspect;
import config.Config;
import config.DatabaseMigrator;
import controller.ProductController;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import service.audit.AuditService;
import servlets.*;
import util.ConnectionPoolManager;
import util.ServiceLocator;

/**
 * Главный класс приложения "Маркетплейс".
 * Координирует инициализацию всех компонентов системы и запуск пользовательского интерфейса.
 * Обеспечивает корректное сохранение данных при завершении работы приложения.
 */
public class ProductCatalogApp {

    /**
     * Точка входа в приложение.
     * Инициализирует все компоненты системы, запускает пользовательский интерфейс
     * и регистрирует обработчик для сохранения данных при завершении работы.
     *
     */
    public static void main(String[] args) {
        try {
            Config config = new Config("dev.yaml");

            ConnectionPoolManager.initialize(config);

            DatabaseMigrator migrator = new DatabaseMigrator();
            migrator.runMigrations(config);

            Runtime.getRuntime().addShutdownHook(new Thread(ConnectionPoolManager::close));

            forceLoadAspects();
            initAspects();
            startEmbeddedServer(config);

        } catch (Exception e) {
            System.err.println("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            ConnectionPoolManager.close();
            System.exit(1);
        }
    }

    private static void startEmbeddedServer(Config config) throws Exception {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(config.getServerPort());

            Context context = tomcat.addContext("", null);

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

            System.out.println("Starting embedded Tomcat...");
            tomcat.getConnector();
            tomcat.start();
            System.out.println("Tomcat started!");
            tomcat.getServer().await();

        } catch (Exception e) {
            throw new RuntimeException("Failed to start embedded server", e);
        }
    }

    private static void forceLoadAspects() {
        try {
            Class.forName("aspect.AuditAspect");
            Class.forName("aspect.PerformanceLoggingAspect");
            System.out.println("Aspects loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load aspects: " + e.getMessage());
        }
    }

    private static void initAspects() {
        try {
            AuditAspect auditAspect = new AuditAspect();
            PerformanceLoggingAspect performanceAspect = new PerformanceLoggingAspect();

            System.out.println("Aspects initialized successfully");
            System.out.println("AuditAspect: " + auditAspect);
            System.out.println("PerformanceAspect: " + performanceAspect);

        } catch (Exception e) {
            System.err.println("Failed to initialize aspects: " + e.getMessage());
            e.printStackTrace();
        }
    }

}