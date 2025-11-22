package ui;

import controller.*;
import model.AuditEntry;
import model.Product;
import model.User;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Консольный пользовательский интерфейс для системы каталога товаров.
 * Предоставляет текстовое меню для взаимодействия с системой через консоль.
 * Обрабатывает ввод пользователя и делегирует бизнес-логику контроллерам.
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthController authController;
    private final ProductController productController;
    private final UserController userController;
    private final AuditController auditController;
    private final MetricsController metricsController;

    private User currentUser = null;

    public ConsoleUI(AuthController authController, ProductController productController,
                     UserController userController, AuditController auditController, MetricsController metricsController) {
        this.authController = authController;
        this.productController = productController;
        this.userController = userController;
        this.auditController = auditController;
        this.metricsController = metricsController;
    }

    /**
     * Запускает главный цикл приложения.
     * Отображает меню аутентификации для неавторизованных пользователей
     * и основное меню для авторизованных пользователей.
     */
    public void start() {
        printHeader();
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showUserMenu();
            }
        }
    }

    /**
     * Выводит заголовок приложения в консоль.
     */
    private void printHeader() {
        String header = """
        ========================================
         Product Catalog Service (Console)
        ========================================
        """;
        System.out.print(header);
    }

    /**
     * Отображает меню аутентификации для неавторизованных пользователей.
     */
    private void showAuthMenu() {
        String menu = """
        
        1) Вход
        2) Регистрация
        3) Выйти
        
        Выберите:\s""";
        System.out.print(menu);

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": login(); break;
            case "2": register(); break;
            case "3": System.exit(0);
            default: System.out.println("Неверный выбор");
        }
    }

    /**
     * Выполняет процесс входа пользователя в систему.
     */
    private void login() {
        System.out.print("Логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();

        Optional<User> userOpt = authController.login(username, password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            System.out.println("Вход выполнен: " + currentUser.getUsername());
        } else {
            System.out.println("Ошибка авторизации");
        }
    }

    /**
     * Выполняет процесс регистрации нового пользователя.
     */
    private void register() {
        System.out.print("Выберите логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();

        boolean success = authController.register(username, password);
        if (success) {
            System.out.println("Пользователь зарегистрирован. Войдите в систему.");
        } else {
            System.out.println("Пользователь с таким именем уже существует");
        }
    }

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    private void logout() {
        if (currentUser != null) {
            authController.logout(currentUser);
            System.out.println("Пользователь " + currentUser.getUsername() + " вышел.");
            currentUser = null;
        }
    }

    /**
     * Отображает главное меню для авторизованных пользователей.
     */
    private void showUserMenu() {
        String menu = """
        
        === Главное меню ===
        1) Добавить товар
        2) Редактировать товар
        3) Удалить товар
        4) Просмотреть товар по ID
        5) Поиск / Фильтрация
        6) Список всех товаров
        7) Метрики
        8) Просмотр аудита (последние записи)
        9) Сменить пользователя (Выйти)
        0) Выйти из приложения
        u) Показать пользователей
        
        Выберите:\s""";
        System.out.print(menu);

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": handleAddProduct(); break;
            case "2": handleEditProduct(); break;
            case "3": handleDeleteProduct(); break;
            case "4": handleViewProduct(); break;
            case "5": handleSearchProducts(); break;
            case "6": handleListAllProducts(); break;
            case "7": showMetrics(); break;
            case "8": showAudit(); break;
            case "9": logout(); break;
            case "0": logout(); return;
            case "u": showUsers(); break;
            default: System.out.println("Неверный выбор");
        }
    }

    /**
     * Обрабатывает добавление нового товара.
     */
    private void handleAddProduct() {
        System.out.print("Название: "); String name = scanner.nextLine().trim();
        System.out.print("Категория: "); String category = scanner.nextLine().trim();
        System.out.print("Бренд: "); String brand = scanner.nextLine().trim();
        System.out.print("Цена: "); double price = safeReadDouble();
        System.out.print("Описание: "); String description = scanner.nextLine().trim();

        Product product = productController.createProduct(currentUser.getUsername(), name, category, brand, price, description, currentUser.getId());
        System.out.println("Добавлено: " + product);
    }

    /**
     * Обрабатывает редактирование существующего товара.
     */
    private void handleEditProduct() {
        System.out.print("ID товара для редактирования: ");
        long id = safeReadLong();

        Optional<Product> productOpt = productController.getProductById(id);
        if (productOpt.isEmpty()) {
            System.out.println("Товар не найден");
            return;
        }

        Product product = productOpt.get();
        System.out.println("Текущий: " + product);

        System.out.print("Новое название (Enter чтобы оставить): ");
        String name = readOptionalString();
        System.out.print("Новая категория (Enter чтобы оставить): ");
        String category = readOptionalString();
        System.out.print("Новый бренд (Enter чтобы оставить): ");
        String brand = readOptionalString();
        System.out.print("Новая цена (Enter чтобы оставить): ");
        Double price = readOptionalDouble();
        System.out.print("Новое описание (Enter чтобы оставить): ");
        String description = readOptionalString();

        Product updatedProduct = productController.updateProduct(currentUser.getUsername(), id, name, category, brand, price, description);
        System.out.println(updatedProduct!=null ? "Обновлено" : "Ошибка обновления");
    }

    /**
     * Обрабатывает удаление товара по идентификатору.
     */
    private void handleDeleteProduct() {
        System.out.print("ID товара для удаления: ");
        long id = safeReadLong();

        boolean success = productController.deleteProduct(currentUser.getUsername(), id);
        System.out.println(success ? "Удалено" : "Не найдено");
    }

    /**
     * Обрабатывает просмотр товара по идентификатору.
     */
    private void handleViewProduct() {
        System.out.print("ID товара: ");
        long id = safeReadLong();

        Optional<Product> product = productController.getProductById(id);
        System.out.println(product.map(Object::toString).orElse("Не найдено"));
    }

    /**
     * Обрабатывает поиск товаров по заданным критериям.
     */
    private void handleSearchProducts() {
        System.out.println("Поиск. Оставьте поле пустым чтобы не фильтровать.");
        System.out.print("Название содержит: "); String name = readOptionalString();
        System.out.print("Категория: "); String category = readOptionalString();
        System.out.print("Бренд: "); String brand = readOptionalString();
        System.out.print("Цена min: "); Double minPrice = readOptionalDouble();
        System.out.print("Цена max: "); Double maxPrice = readOptionalDouble();

        long startTime = System.currentTimeMillis();
        List<Product> results = productController.searchProducts(currentUser.getUsername(), name, category, brand, minPrice, maxPrice);
        long endTime = System.currentTimeMillis();

        System.out.println("Найдено: " + results.size() + " (время: " + (endTime - startTime) + " ms)");
        results.forEach(System.out::println);
    }

    /**
     * Обрабатывает вывод всех товаров системы.
     */
    private void handleListAllProducts() {
        List<Product> allProducts = productController.getAllProducts();
        System.out.println("Всего: " + allProducts.size());
        allProducts.sort(Comparator.comparingLong(Product::getId));
        allProducts.forEach(System.out::println);
    }

    /**
     * Отображает метрики производительности системы.
     */
    private void showMetrics() {
        String metrics = """
        --- Метрики ---
        Всего товаров: %d
        Поисковых запросов: %d
        Среднее время поиска: %.3f ms
        
        """.formatted(
                productController.getTotalProductsCount(),
                metricsController.getSearchCount(),
                metricsController.getAverageSearchTimeMs()
        );
        System.out.print(metrics);
    }

    /**
     * Отображает последние записи аудита.
     */
    private void showAudit() {
            List<AuditEntry> recentEntries = auditController.getRecentAuditEntries(20);
        System.out.println("Последние аудито-записи:");
        recentEntries.forEach(System.out::println);
    }

    /**
     * Отображает список пользователей (только для администраторов).
     */
    private void showUsers() {
        try {
            List<User> users = userController.getUsers(currentUser);
            printUsersList(users);
        } catch (SecurityException e) {
            System.out.println("Нет доступа!");
        }
    }

    /**
     * Выводит форматированный список пользователей.
     *
     * @param users список пользователей для отображения
     */
    private void printUsersList(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("Нет зарегистрированных пользователей.");
            return;
        }

        System.out.println("\nСписок пользователей:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.printf("%d. %s - %s%n", i + 1, user.getUsername(), user.getUserRole());
        }
    }

    /**
     * Читает опциональную строку из консоли.
     * Пустой ввод интерпретируется как null.
     *
     * @return введенная строка или null если ввод пустой
     */
    private String readOptionalString() {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    /**
     * Читает опциональное число с плавающей точкой из консоли.
     * Пустой ввод интерпретируется как null.
     *
     * @return введенное число или null если ввод пустой
     * @throws NumberFormatException если введено некорректное число
     */
    private Double readOptionalDouble() {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : Double.parseDouble(input);
    }

    /**
     * Безопасно читает целое число из консоли с обработкой ошибок.
     * Повторяет запрос до получения корректного числа.
     *
     * @return введенное целое число
     */
    private long safeReadLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Ошибка ввода. Введите число: ");
            }
        }
    }

    /**
     * Безопасно читает число с плавающей точкой из консоли с обработкой ошибок.
     * Повторяет запрос до получения корректного числа.
     *
     * @return введенное число с плавающей точкой
     */
    private double safeReadDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Ошибка ввода. Введите число (например 12.5): ");
            }
        }
    }
}