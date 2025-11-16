package controller;

import model.Product;
import service.product.ProductService;
import service.audit.AuditService;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления операциями с товарами.
 * Обеспечивает создание, обновление, удаление и поиск товаров с ведением журнала аудита.
 */
public class ProductController {
    /** Сервис для операций с товарами */
    private final ProductService productService;

    /** Сервис для записи действий в журнал аудита */
    private final AuditService auditService;

    /**
     * Конструктор контроллера
     *
     * @param productService сервис для операций с товарами
     * @param auditService сервис для записи действий аудита
     * @throws NullPointerException если любой из параметров равен null
     */
    public ProductController(ProductService productService, AuditService auditService) {
        if (productService == null || auditService == null) {
            throw new NullPointerException("ProductService and AuditService cannot be null");
        }
        this.productService = productService;
        this.auditService = auditService;
    }

    /**
     * Создает новый товар и записывает событие в журнал аудита.
     *
     * @param username имя пользователя, создающего товар
     * @param name название товара
     * @param category категория товара
     * @param brand бренд товара
     * @param price цена товара
     * @param description описание товара
     * @return созданный объект товара
     * @throws NullPointerException если любой из строковых параметров равен null
     * @throws IllegalArgumentException если price отрицательный
     */
    public Product createProduct(String username, String name, String category, String brand, double price, String description, Long userId) {
        if (name == null || category == null || brand == null || description == null) {
            throw new NullPointerException("Product fields cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        Product product = productService.createProduct(name, category, brand, price, description, userId);
        auditService.record(username, "ADD_PRODUCT", product.getName());
        return product;
    }

    /**
     * Обновляет существующий товар и записывает событие в журнал аудита при успешном обновлении.
     *
     * @param username имя пользователя, обновляющего товар
     * @param id идентификатор товара для обновления
     * @param name новое название товара (может быть null - поле не обновляется)
     * @param category новая категория товара (может быть null - поле не обновляется)
     * @param brand новый бренд товара (может быть null - поле не обновляется)
     * @param price новая цена товара (может быть null - поле не обновляется)
     * @param description новое описание товара (может быть null - поле не обновляется)
     * @return true если товар успешно обновлен, false если товар не найден
     * @throws IllegalArgumentException если id отрицательный или price отрицательный
     */
    public boolean updateProduct(String username, long id, String name, String category, String brand, Double price, String description) {
        if (id < 0) {
            throw new IllegalArgumentException("Product ID cannot be negative");
        }
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        boolean result = productService.updateProduct(id, name, category, brand, price, description);
        if (result) {
            auditService.record(username, "UPDATE_PRODUCT", "id=" + id);
        }
        return result;
    }

    /**
     * Удаляет товар по идентификатору и записывает событие в журнал аудита при успешном удалении.
     *
     * @param username имя пользователя, удаляющего товар
     * @param id идентификатор товара для удаления
     * @return true если товар успешно удален, false если товар не найден
     * @throws IllegalArgumentException если id отрицательный
     */
    public boolean deleteProduct(String username, long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Product ID cannot be negative");
        }

        boolean result = productService.deleteProduct(id);
        if (result) {
            auditService.record(username, "DELETE_PRODUCT", "id=" + id);
        }
        return result;
    }

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с товаром если найден, или пустой Optional если не найден
     * @throws IllegalArgumentException если id отрицательный
     */
    public Optional<Product> getProductById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Product ID cannot be negative");
        }
        return productService.getProductById(id);
    }

    /**
     * Выполняет поиск товаров по заданным критериям и записывает событие в журнал аудита.
     *
     * @param username имя пользователя, выполняющего поиск
     * @param nameSubstr подстрока для поиска в названии товара (может быть null)
     * @param category категория для фильтрации (может быть null)
     * @param brand бренд для фильтрации (может быть null)
     * @param priceMin минимальная цена для фильтрации (может быть null)
     * @param priceMax максимальная цена для фильтрации (может быть null)
     * @return список товаров, удовлетворяющих критериям поиска (может быть пустым, но не null)
     * @throws IllegalArgumentException если priceMin или priceMax отрицательные,
     *         или если priceMin > priceMax
     */
    public List<Product> searchProducts(String username, String nameSubstr, String category, String brand, Double priceMin, Double priceMax) {
        if (priceMin != null && priceMin < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (priceMax != null && priceMax < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (priceMin != null && priceMax != null && priceMin > priceMax) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        List<Product> results = productService.searchProducts(nameSubstr, category, brand, priceMin, priceMax);
        auditService.record(username, "SEARCH", "found " + results.size() + " products");
        return results;
    }

    /**
     * Возвращает все товары в системе.
     *
     * @return список всех товаров (может быть пустым, но не null)
     */
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Возвращает общее количество товаров в системе.
     *
     * @return количество товаров
     */
    public int getTotalProductsCount() {
        return productService.getTotalProductsCount();
    }
}