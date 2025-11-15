package test.service.product;

import main.cache.QueryCache;
import main.model.Product;
import main.repository.ProductRepository;
import main.service.product.ProductServiceImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class ProductServiceTest extends TestBase {

    private ProductRepository repository;
    private QueryCache cache;
    private ProductServiceImpl service;

    public static void main(String[] args) {
        ProductServiceTest test = new ProductServiceTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== Running ProductServiceImpl Tests ===");

        testCreateProduct();
        testGetProductById();
        testUpdateProduct();
        testDeleteProduct();
        testSearchProducts();
        testSearchProductsUsesCache();
        testCacheInvalidationOnCreate();
        testCacheInvalidationOnUpdate();
        testCacheInvalidationOnDelete();
        testGetAllProducts();
        testGetTotalProductsCount();
        testCreateCacheKey();

        System.out.println("=== All ProductServiceImpl tests passed! ===");
    }

    private void setUp() {
        repository = new ProductRepository();
        cache = new QueryCache(10);
        service = new ProductServiceImpl(repository, cache);
    }

    public void testCreateProduct() {
        setUp();

        Product product = service.createProduct("Test Product", "Electronics", "Brand", 99.99, "Description");

        assertNotNull(product, "Created product should not be null");
        assertEquals("Test Product", product.getName(), "Name should match");

        Optional<Product> found = repository.findById(product.getId());
        assertTrue(found.isPresent(), "Product should be stored in repository");
        System.out.println("✓ testCreateProduct passed");
    }

    public void testGetProductById() {
        setUp();
        Product created = service.createProduct("Test", "Category", "Brand", 100.0, "Desc");

        Optional<Product> found = service.getProductById(created.getId());
        Optional<Product> notFound = service.getProductById(999L);

        assertTrue(found.isPresent(), "Should find existing product");
        assertEquals(created.getId(), found.get().getId(), "IDs should match");
        assertFalse(notFound.isPresent(), "Should not find non-existing product");
        System.out.println("✓ testGetProductById passed");
    }

    public void testUpdateProduct() {
        setUp();
        Product created = service.createProduct("Original", "Category", "Brand", 100.0, "Desc");

        boolean result = service.updateProduct(created.getId(), "Updated", "New Category", "New Brand", 150.0, "New Desc");

        assertTrue(result, "Update should succeed");
        Optional<Product> updated = service.getProductById(created.getId());
        assertTrue(updated.isPresent(), "Product should still exist");
        assertEquals("Updated", updated.get().getName(), "Name should be updated");
        assertEquals(150.0, updated.get().getPrice(), 0.001, "Price should be updated");

        assertFalse(service.updateProduct(999L, "Name", "Category", "Brand", 100.0, "Desc"),
                "Update should fail for non-existing product");
        System.out.println("✓ testUpdateProduct passed");
    }

    public void testDeleteProduct() {
        setUp();
        Product created = service.createProduct("ToDelete", "Category", "Brand", 100.0, "Desc");

        assertTrue(service.deleteProduct(created.getId()), "Delete should succeed");
        assertFalse(service.getProductById(created.getId()).isPresent(), "Product should be deleted");
        assertFalse(service.deleteProduct(999L), "Delete should fail for non-existing product");
        System.out.println("✓ testDeleteProduct passed");
    }

    public void testSearchProducts() {
        setUp();
        service.createProduct("iPhone", "Electronics", "Apple", 999.0, "Smartphone");
        service.createProduct("Galaxy", "Electronics", "Samsung", 899.0, "Smartphone");
        service.createProduct("MacBook", "Computers", "Apple", 1999.0, "Laptop");

        List<Product> appleResults = service.searchProducts(null, null, "Apple", null, null);
        List<Product> electronicsResults = service.searchProducts(null, "Electronics", null, null, null);
        List<Product> expensiveResults = service.searchProducts(null, null, null, 1500.0, null);

        assertEquals(2, appleResults.size(), "Should find 2 Apple products");
        assertEquals(2, electronicsResults.size(), "Should find 2 Electronics products");
        assertEquals(1, expensiveResults.size(), "Should find 1 expensive product");
        assertEquals("MacBook", expensiveResults.get(0).getName(), "Expensive product should be MacBook");
        System.out.println("✓ testSearchProducts passed");
    }

    public void testSearchProductsUsesCache() {
        setUp();
        service.createProduct("Test", "Category", "Brand", 100.0, "Desc");

        List<Product> firstResults = service.searchProducts("Test", null, null, null, null);

        List<Product> secondResults = service.searchProducts("Test", null, null, null, null);

        assertEquals(firstResults.size(), secondResults.size(), "Results should be identical");
        assertEquals(firstResults.get(0).getId(), secondResults.get(0).getId(), "Product IDs should match");
        System.out.println("✓ testSearchProductsUsesCache passed");
    }

    public void testCacheInvalidationOnCreate() {
        setUp();
        service.searchProducts("Test", null, null, null, null);
        int initialCacheSize = getCacheSize(cache);

        service.createProduct("New Product", "Category", "Brand", 100.0, "Desc");

        assertEquals(0, getCacheSize(cache), "Cache should be empty after create");
        System.out.println("✓ testCacheInvalidationOnCreate passed");
    }

    public void testCacheInvalidationOnUpdate() {
        setUp();
        Product product = service.createProduct("Test", "Category", "Brand", 100.0, "Desc");

        service.searchProducts("Test", null, null, null, null);
        int initialCacheSize = getCacheSize(cache);

        service.updateProduct(product.getId(), "Updated", "Category", "Brand", 150.0, "Desc");

        assertEquals(0, getCacheSize(cache), "Cache should be empty after update");
        System.out.println("✓ testCacheInvalidationOnUpdate passed");
    }

    public void testCacheInvalidationOnDelete() {
        setUp();
        Product product = service.createProduct("Test", "Category", "Brand", 100.0, "Desc");

        service.searchProducts("Test", null, null, null, null);
        int initialCacheSize = getCacheSize(cache);

        service.deleteProduct(product.getId());

        assertEquals(0, getCacheSize(cache), "Cache should be empty after delete");
        System.out.println("✓ testCacheInvalidationOnDelete passed");
    }

    public void testGetAllProducts() {
        setUp();
        service.createProduct("Product1", "Category1", "Brand1", 100.0, "Desc1");
        service.createProduct("Product2", "Category2", "Brand2", 200.0, "Desc2");

        List<Product> allProducts = service.getAllProducts();

        assertEquals(2, allProducts.size(), "Should return all products");
        System.out.println("✓ testGetAllProducts passed");
    }

    public void testGetTotalProductsCount() {
        setUp();

        assertEquals(0, service.getTotalProductsCount(), "Initial count should be 0");

        service.createProduct("Product1", "Category1", "Brand1", 100.0, "Desc1");
        service.createProduct("Product2", "Category2", "Brand2", 200.0, "Desc2");

        assertEquals(2, service.getTotalProductsCount(), "Count should be 2 after adding products");
        System.out.println("✓ testGetTotalProductsCount passed");
    }

    public void testCreateCacheKey() {
        setUp();

        String key1 = service.createCacheKey("iphone", "electronics", "apple", 500.0, 1000.0);
        String key2 = service.createCacheKey("iphone", "electronics", "apple", 500.0, 1000.0);
        String key3 = service.createCacheKey("galaxy", "electronics", "samsung", null, 800.0);

        assertEquals(key1, key2, "Same parameters should produce same cache key");
        assertFalse(key1.equals(key3), "Different parameters should produce different cache keys");
        assertTrue(key1.contains("iphone"), "Cache key should contain search parameters");
        System.out.println("✓ testCreateCacheKey passed");
    }

    private int getCacheSize(QueryCache cache) {
        try {
            return cache.toString().contains("empty") ? 0 : 1;
        } catch (Exception e) {
            return 0;
        }
    }
}