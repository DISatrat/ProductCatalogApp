package service.product;

import model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления товарами в системе каталога.
 * Предоставляет операции для создания, обновления, удаления и поиска товаров.
 */
public interface ProductService {
    /**
     * Создает новый товар с указанными параметрами.
     *
     * @param name название товара
     * @param category категория товара
     * @param brand бренд товара
     * @param price цена товара
     * @param description описание товара
     * @return созданный объект товара
     */
    Product createProduct(String name, String category, String brand, double price, String description, Long userId);

    /**
     * Обновляет существующий товар по идентификатору.
     * Параметры могут быть null - в этом случае соответствующие поля не обновляются.
     *
     * @param id идентификатор товара для обновления
     * @param name новое название товара
     * @param category новая категория товара
     * @param brand новый бренд товара
     * @param price новая цена товара
     * @param description новое описание товара
     * @return true если товар успешно обновлен, false если товар не найден
     */
    boolean updateProduct(long id, String name, String category, String brand, Double price, String description);

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара для удаления
     * @return true если товар успешно удален, false если товар не найден
     */
    boolean deleteProduct(long id);

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с товаром если найден, или пустой Optional если не найден
     */
    Optional<Product> getProductById(long id);

    /**
     * Выполняет поиск товаров по заданным критериям.
     * Все параметры могут быть null - в этом случае соответствующий критерий не применяется.
     *
     * @param nameSubstr подстрока для поиска в названии товара
     * @param category категория для фильтрации
     * @param brand бренд для фильтрации
     * @param priceMin минимальная цена для фильтрации
     * @param priceMax максимальная цена для фильтрации
     * @return список товаров, удовлетворяющих критериям поиска
     */
    List<Product> searchProducts(String nameSubstr, String category, String brand, Double priceMin, Double priceMax);

    /**
     * Создает ключ кэша на основе параметров поиска.
     *
     * @param nameSubstr подстрока для поиска в названии товара
     * @param category категория для фильтрации
     * @param brand бренд для фильтрации
     * @param priceMin минимальная цена для фильтрации
     * @param priceMax максимальная цена для фильтрации
     * @return строковый ключ для кэширования результатов поиска
     */
    String createCacheKey(String nameSubstr, String category, String brand, Double priceMin, Double priceMax);

    /**
     * Возвращает общее количество товаров в системе.
     *
     * @return количество товаров
     */
    int getTotalProductsCount();

    /**
     * Возвращает все товары в системе.
     *
     * @return список всех товаров
     */
    List<Product> getAllProducts();
}