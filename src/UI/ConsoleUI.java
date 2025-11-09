package UI;

import Repository.ProductRepository;
import Repository.UserRepository;
import model.AuditEntry;
import model.Product;
import model.User;
import service.AuditService;
import service.CatalogService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final CatalogService service;
    private final ProductRepository repo;
    private final UserRepository userRepo;
    private final AuditService audit;

    private User currentUser = null;

    public ConsoleUI(CatalogService service, ProductRepository repo, UserRepository userRepo, AuditService audit) {
        this.service = service;
        this.repo = repo;
        this.userRepo = userRepo;
        this.audit = audit;
    }

    public void start() {
        printHeader();
        mainLoop:
        while (true) {
            if (currentUser == null) {
                System.out.println("\n1) Вход  2) Регистрация  3) Выйти");
                System.out.print("Выберите: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1": login(); break;
                    case "2": register(); break;
                    case "3": break mainLoop;
                    default: System.out.println("Неверный выбор");
                }
            } else {
                userMenu();
            }
        }
        System.out.println("Завершение работы. Сохраняю данные...");
    }

    private void printHeader() {
        System.out.println("========================================");
        System.out.println(" Product Catalog Service (Console)");
        System.out.println("========================================");
    }

    private void login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String pass = scanner.nextLine().trim();
        Optional<User> uOpt = userRepo.findByUsername(login);
        if (uOpt.isPresent() && checkPassword(pass, uOpt.get().getPasswordHash())) {
            currentUser = uOpt.get();
            audit.record(currentUser.getUsername(), "LOGIN", "success");
            System.out.println("Вход выполнен: " + currentUser.getUsername());
        } else {
            System.out.println("Ошибка авторизации");
        }
    }

    private void register() {
        System.out.print("Выберите логин: ");
        String login = scanner.nextLine().trim();
        if (userRepo.findByUsername(login).isPresent()) {
            System.out.println("Пользователь с таким именем уже существует");
            return;
        }
        System.out.print("Пароль: ");
        String pass = scanner.nextLine().trim();
        String hash = hashPassword(pass);
        User u = new User(login, hash);
        userRepo.addUser(u);
        audit.record(login, "REGISTER", "created user");
        System.out.println("Пользователь зарегистрирован. Войдите в систему.");
    }

    private void logout() {
        if (currentUser != null) {
            audit.record(currentUser.getUsername(), "LOGOUT", "user logged out");
            System.out.println("Пользователь " + currentUser.getUsername() + " вышел.");
            currentUser = null;
        }
    }

    private void userMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1) Добавить товар");
        System.out.println("2) Редактировать товар");
        System.out.println("3) Удалить товар");
        System.out.println("4) Просмотреть товар по ID");
        System.out.println("5) Поиск / Фильтрация");
        System.out.println("6) Список всех товаров (кол-во: " + service.totalProducts() + ")");
        System.out.println("7) Метрики");
        System.out.println("8) Просмотр аудита (последние записи)");
        System.out.println("9) Сменить пользователя (Выйти)");
        System.out.println("0) Выйти из приложения");
        System.out.print("Выберите: ");
        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1": handleAdd(); break;
            case "2": handleEdit(); break;
            case "3": handleDelete(); break;
            case "4": handleViewById(); break;
            case "5": handleSearch(); break;
            case "6": handleListAll(); break;
            case "7": showMetrics(); break;
            case "8": showAudit(); break;
            case "9": logout(); break;
            case "0": logout(); return;
            default: System.out.println("Неверный выбор");
        }
    }

    private void handleAdd() {
        System.out.print("Название: "); String name = scanner.nextLine().trim();
        System.out.print("Категория: "); String cat = scanner.nextLine().trim();
        System.out.print("Бренд: "); String brand = scanner.nextLine().trim();
        System.out.print("Цена: "); double price = safeReadDouble();
        System.out.print("Описание: "); String desc = scanner.nextLine().trim();
        Product p = service.addProduct(currentUser.getUsername(), name, cat, brand, price, desc);
        System.out.println("Добавлено: " + p);
    }

    private void handleEdit() {
        System.out.print("ID товара для редактирования: ");
        long id = safeReadLong();
        Optional<Product> pOpt = service.getById(id);
        if (!pOpt.isPresent()) { System.out.println("Товар не найден"); return; }
        Product p = pOpt.get();
        System.out.println("Текущий: " + p);
        System.out.print("Новое название (Enter чтобы оставить): "); String name = scanner.nextLine().trim(); if (name.isEmpty()) name = null;
        System.out.print("Новая категория (Enter чтобы оставить): "); String cat = scanner.nextLine().trim(); if (cat.isEmpty()) cat = null;
        System.out.print("Новый бренд (Enter чтобы оставить): "); String brand = scanner.nextLine().trim(); if (brand.isEmpty()) brand = null;
        System.out.print("Новая цена (Enter чтобы оставить): "); String priceStr = scanner.nextLine().trim(); Double price = null; if (!priceStr.isEmpty()) { price = Double.parseDouble(priceStr); }
        System.out.print("Новое описание (Enter чтобы оставить): "); String desc = scanner.nextLine().trim(); if (desc.isEmpty()) desc = null;
        boolean ok = service.updateProduct(currentUser.getUsername(), id, name, cat, brand, price, desc);
        System.out.println(ok ? "Обновлено" : "Ошибка обновления");
    }

    private void handleDelete() {
        System.out.print("ID товара для удаления: ");
        long id = safeReadLong();
        boolean ok = service.deleteProduct(currentUser.getUsername(), id);
        System.out.println(ok ? "Удалено" : "Не найдено");
    }

    private void handleViewById() {
        System.out.print("ID товара: ");
        long id = safeReadLong();
        Optional<Product> p = service.getById(id);
        System.out.println(p.map(Object::toString).orElse("Не найдено"));
    }

    private void handleSearch() {
        System.out.println("Поиск. Оставьте поле пустым чтобы не фильтровать.");
        System.out.print("Название содержит: "); String name = scanner.nextLine().trim(); if (name.isEmpty()) name = null;
        System.out.print("Категория: "); String category = scanner.nextLine().trim(); if (category.isEmpty()) category = null;
        System.out.print("Бренд: "); String brand = scanner.nextLine().trim(); if (brand.isEmpty()) brand = null;
        System.out.print("Цена min: "); String minStr = scanner.nextLine().trim(); Double min = minStr.isEmpty() ? null : Double.parseDouble(minStr);
        System.out.print("Цена max: "); String maxStr = scanner.nextLine().trim(); Double max = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
        long t0 = System.currentTimeMillis();
        List<Product> results = service.search(currentUser.getUsername(), name, category, brand, min, max);
        long t1 = System.currentTimeMillis();
        System.out.println("Найдено: " + results.size() + " (время: " + (t1 - t0) + " ms)");
        for (Product p : results) {
            System.out.println(p);
        }
    }

    private void handleListAll() {
        List<Product> all = repo.findAll();
        System.out.println("Всего: " + all.size());
        all.sort(Comparator.comparingLong(Product::getId));
        for (Product p : all) System.out.println(p);
    }

    private void showMetrics() {
        System.out.println("--- Метрики ---");
        System.out.println("Всего товаров: " + service.totalProducts());
        System.out.println("Поисковых запросов: " + service.getSearchCount());
        System.out.printf("Среднее время поиска: %.3f ms%n", service.getAverageSearchMs());
        System.out.println();
    }

    private void showAudit() {
        List<AuditEntry> entries = audit.getEntries();
        int from = Math.max(0, entries.size() - 20);
        List<AuditEntry> last = entries.subList(from, entries.size());
        System.out.println("Последние аудито-записи:");
        for (AuditEntry e : last) System.out.println(e);
    }

    private long safeReadLong() {
        while (true) {
            try {
                String s = scanner.nextLine().trim();
                return Long.parseLong(s);
            } catch (Exception ex) {
                System.out.print("Ошибка ввода. Введите число: ");
            }
        }
    }

    private double safeReadDouble() {
        while (true) {
            try {
                String s = scanner.nextLine().trim();
                return Double.parseDouble(s);
            } catch (Exception ex) {
                System.out.print("Ошибка ввода. Введите число (например 12.5): ");
            }
        }
    }

    private String hashPassword(String pass) {
        return Integer.toString(pass.hashCode());
    }

    private boolean checkPassword(String pass, String hash) {
        return hashPassword(pass).equals(hash);
    }
}
