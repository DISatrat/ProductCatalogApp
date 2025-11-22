package repository.product;

import model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления товарами в базе данных.
 * Обеспечивает операции CRUD и поиска для товаров.
 */
public interface ProductRepository {

    /**
     * Создает новый товар в базе данных.
     *
     * @param name название товара
     * @param category категория товара
     * @param brand бренд товара
     * @param price цена товара
     * @param description описание товара
     * @param userId ID пользователя, создающего товар
     * @return созданный товар с присвоенным ID
     * @throws RuntimeException если не удалось создать товар
     */
    Product create(String name, String category, String brand, double price, String description, Long userId);

    /**
     * Обновляет существующий товар.
     *
     * @param id ID товара для обновления
     * @param name новое название
     * @param category новая категория
     * @param brand новый бренд
     * @param price новая цена
     * @param description новое описание
     * @return Product
     */
    Product update(long id, String name, String category, String brand, Double price, String description);

    /**
     * Находит товар по ID.
     *
     * @param id ID товара для поиска
     * @return Optional с товаром если найден, или пустой Optional если не найден
     */
    Optional<Product> findById(long id);

    /**
     * Возвращает список всех товаров.
     *
     * @return список всех товаров (может быть пустым, но не null)
     */
    List<Product> findAll();

    /**
     * Удаляет товар по ID.
     *
     * @param id ID товара для удаления
     * @return true если товар был удален, false если не найден
     */
    boolean delete(long id);

    /**
     * Частично обновляет товар (только указанные поля).
     *
     * @param id ID товара для обновления
     * @param name новое название (может быть null)
     * @param category новая категория (может быть null)
     * @param brand новый бренд (может быть null)
     * @param price новая цена (может быть null)
     * @param description новое описание (может быть null)
     * @return true если товар был обновлен, false если не найден
     */
    boolean updatePartial(long id, String name, String category, String brand, Double price, String description);

    /**
     * Выполняет поиск товаров с фильтрацией.
     *
     * @param nameSubstring подстрока для поиска в названии (может быть null)
     * @param category категория для фильтрации (может быть null)
     * @param brand бренд для фильтрации (может быть null)
     * @param minPrice минимальная цена (может быть null)
     * @param maxPrice максимальная цена (может быть null)
     * @return список товаров, соответствующих критериям поиска
     */
    List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice);

    /**
     * Возвращает общее количество товаров в базе данных.
     *
     * @return количество товаров
     */
    int getCount();
}
