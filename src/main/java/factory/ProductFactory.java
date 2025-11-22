package factory;

import repository.product.ProductRepositoryImpl;


/**
 * Фабрика для создания и управления товаров.
 * Обеспечивает загрузку товаров из постоянного хранилища и сохранение обратно.
 */
public class ProductFactory {

    /**
     * Создает и инициализирует репозиторий товаров для работы с БД.
     *
     * @return инициализированный репозиторий товаров
     */
    public static ProductRepositoryImpl createProductRepository() {
        return new ProductRepositoryImpl();
    }
}