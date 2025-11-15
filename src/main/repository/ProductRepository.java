package main.repository;

import main.model.Product;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProductRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Long, Product> products = new HashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Восстанавливает генератор ID после десериализации
     */
    public void restoreIdGenerator() {
        long maxId = products.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        idGenerator.set(maxId + 1);
    }

    /**
     * Создает новый продукт
     */
    public Product create(String name, String category, String brand, double price, String description) {
        long newId = idGenerator.getAndIncrement();
        Product newProduct = new Product(newId, name, category, brand, price, description);
        products.put(newId, newProduct);
        return newProduct;
    }

    /**
     * Обновляет существующий продукт
     */
    public boolean update(long id, String name, String category, String brand, Double price, String description) {
        if (id == 0) {
            throw new IllegalArgumentException("Product must have valid ID for update");
        }
        if(!products.containsKey(id)) {
            return false;
        }
        Product product = new Product(id, name, category, brand, price, description);
        products.put(id, product);
        return true;
    }

    /**
     * Находит продукт по ID
     */
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    /**
     * Возвращает все продукты
     */
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Удаляет продукт по ID
     * @return true если продукт был удален, false если не найден
     */
    public boolean delete(long id) {
        return products.remove(id) != null;
    }

    /**
     * Обновляет отдельные поля продукта
     */
    public boolean updatePartial(long id, String name, String category, String brand, Double price, String description) {
        Product product = products.get(id);
        if (product == null) {
            return false;
        }

        if (name != null) product.setName(name);
        if (category != null) product.setCategory(category);
        if (brand != null) product.setBrand(brand);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);

        return true;
    }

    /**
     * Поиск продуктов с фильтрацией
     */
    public List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice) {
        return products.values().stream()
                .filter(product -> matchesName(product, nameSubstring))
                .filter(product -> matchesCategory(product, category))
                .filter(product -> matchesBrand(product, brand))
                .filter(product -> matchesPriceRange(product, minPrice, maxPrice))
                .sorted(Comparator.comparingLong(Product::getId))
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, соответствует ли название товара заданной подстроке.
     * Поиск выполняется без учета регистра. Если подстрока не задана, товар считается соответствующим.
     *
     * @param product товар для проверки
     * @param nameSubstring подстрока для поиска в названии товара (может быть null или пустой)
     * @return true если товар соответствует критерию названия, false в противном случае
     */
    private boolean matchesName(Product product, String nameSubstring) {
        return nameSubstring == null || nameSubstring.isEmpty() ||
                product.getName().toLowerCase().contains(nameSubstring.toLowerCase());
    }

    /**
     * Проверяет, соответствует ли категория товара заданной категории.
     * Сравнение выполняется без учета регистра. Если категория не задана, товар считается соответствующим.
     *
     * @param product товар для проверки
     * @param category категория для фильтрации (может быть null или пустой)
     * @return true если товар соответствует критерию категории, false в противном случае
     */
    private boolean matchesCategory(Product product, String category) {
        return category == null || category.isEmpty() ||
                product.getCategory().equalsIgnoreCase(category);
    }

    /**
     * Проверяет, соответствует ли бренд товара заданному бренду.
     * Сравнение выполняется без учета регистра. Если бренд не задан, товар считается соответствующим.
     *
     * @param product товар для проверки
     * @param brand бренд для фильтрации (может быть null или пустой)
     * @return true если товар соответствует критерию бренда, false в противном случае
     */
    private boolean matchesBrand(Product product, String brand) {
        return brand == null || brand.isEmpty() ||
                product.getBrand().equalsIgnoreCase(brand);
    }

    /**
     * Проверяет, находится ли цена товара в заданном диапазоне.
     * Если границы диапазона не заданы, они считаются неограниченными.
     *
     * @param product товар для проверки
     * @param minPrice минимальная цена диапазона (может быть null)
     * @param maxPrice максимальная цена диапазона (может быть null)
     * @return true если цена товара находится в заданном диапазоне, false в противном случае
     */
    private boolean matchesPriceRange(Product product, Double minPrice, Double maxPrice) {
        double productPrice = product.getPrice();
        boolean matchesMin = minPrice == null || productPrice >= minPrice;
        boolean matchesMax = maxPrice == null || productPrice <= maxPrice;
        return matchesMin && matchesMax;
    }

    /**
     * Возвращает количество продуктов
     */
    public int getCount() {
        return products.size();
    }

    /**
     * Очищает репозиторий (в основном для тестирования)
     */
    public void clear() {
        products.clear();
        idGenerator.set(1);
    }

    /**
     * Возвращает все посты
     */
    public int count() {
        return products.size();
    }
}