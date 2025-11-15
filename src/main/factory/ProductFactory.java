package main.factory;

import main.model.Product;
import main.repository.ProductRepository;
import main.util.PersistenceUtil;

import java.util.List;

/**
 * Фабрика для создания и управления репозиториями товаров.
 * Обеспечивает загрузку товаров из постоянного хранилища и сохранение обратно.
 */
public class ProductFactory {

    /**
     * Создает и инициализирует репозиторий товаров.
     * Загружает существующие товары из файла "products.dat" если он существует.
     * Если файл не существует или пуст, создается пустой репозиторий.
     * После загрузки восстанавливает генератор идентификаторов для согласованности ID.
     *
     * @return инициализированный репозиторий товаров
     */
    public static ProductRepository createProductRepository() {
        ProductRepository repo = new ProductRepository();

        List<Product> products = PersistenceUtil.loadObject("products.dat", List.class);
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                repo.create(
                        product.getName(),
                        product.getCategory(),
                        product.getBrand(),
                        product.getPrice(),
                        product.getDescription()
                );
            }
            repo.restoreIdGenerator();
        }
        return repo;
    }

    /**
     * Сохраняет все товары из репозитория в файл "products.dat".
     * Используется для сохранения состояния данных между запусками приложения.
     *
     * @param productRepo репозиторий товаров для сохранения
     */
    public static void saveData(ProductRepository productRepo) {
        PersistenceUtil.saveObject(productRepo.findAll(), "products.dat");
    }
}