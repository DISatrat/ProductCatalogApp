package com.example.productcatalog.repository.product;

import com.example.productcatalog.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для операций с сущностью Product.
 */
public interface ProductRepository {

    /**
     * Создает новый продукт.
     *
     * @param product продукт для создания
     * @return созданный продукт с сгенерированным ID
     */
    Product save(Product product);

    /**
     * Обновляет существующий продукт.
     *
     * @param product продукт с обновленными данными
     * @return обновленный продукт
     */
    Product update(Product product);

    /**
     * Находит продукт по ID.
     *
     * @param id идентификатор продукта
     * @return Optional с продуктом, если найден
     */
    Optional<Product> findById(Long id);

    /**
     * Получает все продукты.
     *
     * @return список всех продуктов
     */
    List<Product> findAll();

    /**
     * Удаляет продукт по ID.
     *
     * @param id идентификатор продукта
     * @return true, если продукт был удален
     */
    boolean deleteById(Long id);

    /**
     * Поиск продуктов по различным критериям.
     *
     * @param nameSubstring подстрока для поиска в названии продукта
     * @param category      фильтр по категории
     * @param brand         фильтр по бренду
     * @param minPrice      фильтр минимальной цены
     * @param maxPrice      фильтр максимальной цены
     * @return список найденных продуктов
     */
    List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice);

    /**
     * Получает общее количество продуктов.
     *
     * @return общее количество продуктов
     */
    int count();
}
