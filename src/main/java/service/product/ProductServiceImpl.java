package service.product;

import cache.QueryCache;
import model.Product;
import repository.product.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления товарами с поддержкой кэширования запросов.
 * Автоматически инвалидирует кэш при изменении данных товаров.
 */
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final QueryCache queryCache;

    public ProductServiceImpl(ProductRepository productRepository, QueryCache queryCache) {
        this.productRepository = productRepository;
        this.queryCache = queryCache;
    }

    @Override
    public Product createProduct(String name, String category, String brand, double price, String description, Long userId) {
        Product product = productRepository.create(name, category, brand, price, description, userId);
        queryCache.invalidateAll();
        return product;
    }

    @Override
    public boolean updateProduct(long id, String name, String category, String brand, Double price, String description) {
        boolean isUpdated = productRepository.update(id, name, category, brand, price, description);
        if (isUpdated) {
            queryCache.invalidateAll();
        }
        return isUpdated;
    }

    @Override
    public boolean deleteProduct(long id) {
        boolean isDeleted = productRepository.delete(id);
        if (isDeleted) {
            queryCache.invalidateAll();
        }
        return isDeleted;
    }

    @Override
    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> searchProducts(String nameSubstr, String category, String brand, Double priceMin, Double priceMax) {
        String cacheKey = createCacheKey(nameSubstr, category, brand, priceMin, priceMax);

        List<Product> cachedResult = queryCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<Product> result = productRepository.search(nameSubstr, category, brand, priceMin, priceMax);
        queryCache.put(cacheKey, result);
        return result;
    }

    @Override
    public int getTotalProductsCount() {
        return productRepository.getCount();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public String createCacheKey(String nameSubstr, String category, String brand, Double priceMin, Double priceMax) {
        return String.format("n=%s|c=%s|b=%s|min=%s|max=%s",
                nameSubstr, category, brand, priceMin, priceMax);
    }
}
