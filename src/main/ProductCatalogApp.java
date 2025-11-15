package main;

import main.controller.*;
import main.factory.ProductFactory;
import main.factory.UserFactory;
import main.repository.ProductRepository;
import main.repository.UserRepository;
import main.service.audit.AuditService;
import main.service.audit.AuditServiceImpl;
import main.service.metrics.MetricsServiceImpl;
import main.service.metrics.MetricsService;
import main.service.product.ProductService;
import main.service.product.ProductServiceImpl;
import main.service.user.UserService;
import main.service.user.UserServiceImpl;
import main.ui.ConsoleUI;
import main.cache.QueryCache;

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
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация репозиториев с загрузкой данных из постоянного хранилища
        ProductRepository productRepo = ProductFactory.createProductRepository();
        UserRepository userRepo = UserFactory.createUserRepository();

        // Создание пользовательского интерфейса
        ConsoleUI ui = getConsoleUI(productRepo, userRepo);

        // Регистрация обработчика завершения работы для сохранения данных
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving data...");
            ProductFactory.saveData(productRepo);
            UserFactory.saveData(userRepo);
            System.out.println("Data saved successfully");
        }));

        ui.start();
    }

    /**
     * Создает и конфигурирует консольный пользовательский интерфейс.
     * Инициализирует все необходимые сервисы и контроллеры, устанавливает зависимости между ними.
     *
     * @param productRepo репозиторий товаров
     * @param userRepo репозиторий пользователей
     * @return сконфигурированный экземпляр консольного интерфейса
     */
    private static ConsoleUI getConsoleUI(ProductRepository productRepo, UserRepository userRepo) {
        // Инициализация сервисов
        AuditService audit = new AuditServiceImpl();
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