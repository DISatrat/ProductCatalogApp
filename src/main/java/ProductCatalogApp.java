import cache.QueryCache;
import config.Config;
import config.DatabaseMigrator;
import controller.*;
import factory.AuditFactory;
import factory.ProductFactory;
import factory.UserFactory;
import repository.audit.AuditRepositoryImpl;
import repository.product.ProductRepositoryImpl;
import repository.user.UserRepositoryImpl;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;
import service.metrics.MetricsService;
import service.metrics.MetricsServiceImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import service.user.UserService;
import service.user.UserServiceImpl;
import ui.ConsoleUI;
import util.ConnectionHolder;

import java.sql.Connection;
import java.sql.DriverManager;

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
        ConnectionHolder connectionHolder = new ConnectionHolder();

        try {
            Config config = new Config("dev.yaml");

            Connection connection = DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUsername(),
                    config.getDbPassword()
            );
            connectionHolder.setConnection(connection);

            DatabaseMigrator migrator = new DatabaseMigrator();
            migrator.runMigrations(config);

            ProductRepositoryImpl productRepo = ProductFactory.createProductRepository(connection);
            UserRepositoryImpl userRepo = UserFactory.createUserRepository(connection);
            AuditRepositoryImpl auditRepo = AuditFactory.createAuditRepository(connection);
            ConsoleUI ui = getConsoleUI(productRepo, userRepo, auditRepo);

            Runtime.getRuntime().addShutdownHook(new Thread(connectionHolder::close));

            ui.start();

        } catch (Exception e) {
            System.err.println("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            connectionHolder.close();
            System.exit(1);
        }
    }

    /**
     * Создает и конфигурирует консольный пользовательский интерфейс.
     * Инициализирует все необходимые сервисы и контроллеры, устанавливает зависимости между ними.
     *
     * @param productRepo репозиторий товаров
     * @param userRepo репозиторий пользователей
     * @param auditRepo репозиторий аудита
     * @return сконфигурированный экземпляр консольного интерфейса
     */
    private static ConsoleUI getConsoleUI(ProductRepositoryImpl productRepo, UserRepositoryImpl userRepo, AuditRepositoryImpl auditRepo) {
        AuditService audit = new AuditServiceImpl(auditRepo);
        MetricsService metricsService = new MetricsServiceImpl();
        QueryCache cache = new QueryCache(100);
        ProductService productService = new ProductServiceImpl(productRepo, cache);
        UserService userService = new UserServiceImpl(userRepo);

        return new ConsoleUI(
                new AuthController(userService, audit),
                new ProductController(productService, audit),
                new UserController(userService),
                new AuditController(audit),
                new MetricsController(metricsService)
        );
    }
}