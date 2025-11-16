package factory;

import repository.product.ProductRepositoryImpl;

import java.sql.Connection;

/**
 * Фабрика для создания и управления товаров.
 * Обеспечивает загрузку товаров из постоянного хранилища и сохранение обратно.
 */
public class ProductFactory {

    /**
     * Создает и инициализирует репозиторий товаров для работы с БД.
     *
     * @param connection соединение с БД
     * @return инициализированный репозиторий товаров
     */
    public static ProductRepositoryImpl createProductRepository(Connection connection) {
        return new ProductRepositoryImpl(connection);
    }
}