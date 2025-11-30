package com.example.productcatalog.service;

import com.example.productcatalog.dto.ProductRequestDTO;
import com.example.productcatalog.dto.ProductSearchDTO;
import com.example.productcatalog.dto.ProductUpdateDTO;
import com.example.productcatalog.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для операций с продуктами.
 */
public interface ProductService {

    /**
     * Создает новый продукт.
     *
     * @param request запрос на создание продукта
     * @param userId  идентификатор пользователя, создающего продукт
     * @return созданный продукт
     */
    Product createProduct(ProductRequestDTO request, Long userId);

    /**
     * Обновляет существующий продукт.
     *
     * @param id      идентификатор продукта
     * @param request запрос на обновление продукта
     * @return обновленный продукт
     */
    Product updateProduct(Long id, ProductUpdateDTO request);

    /**
     * Находит продукт по идентификатору.
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
    List<Product> getAllProducts();

    /**
     * Удаляет продукт по идентификатору.
     *
     * @param id идентификатор продукта
     * @return true, если продукт был удален
     */
    boolean deleteProduct(Long id);

    /**
     * Поиск продуктов по критериям.
     *
     * @param searchDTO критерии поиска
     * @return список найденных продуктов
     */
    List<Product> searchProducts(ProductSearchDTO searchDTO);

    /**
     * Получает общее количество продуктов.
     *
     * @return общее количество продуктов
     */
    int getTotalCount();
}
