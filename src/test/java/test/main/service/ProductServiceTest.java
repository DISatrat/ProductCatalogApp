package test.main.service;

import cache.QueryCache;
import model.Product;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class ProductServiceTest extends TestBase {

    private ProductService productService;
    private ProductRepository productRepository;
    private QueryCache queryCache;

    public static void main(String[] args) {
        try {
            ProductServiceTest test = new ProductServiceTest();
            test.runAllTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllTests() throws Exception {
        setUp();

        testCreateProduct();
        testGetProductById();
        testUpdateProduct();
        testDeleteProduct();
        testSearchProducts();
        testGetAllProducts();
        testGetTotalProductsCount();
        testCacheInvalidation();

        tearDown();
        System.out.println("All ProductService tests passed!");
    }

    private void setUp() throws Exception {
        TestBase.setUpAll();
        productRepository = new ProductRepositoryImpl(connection);
        queryCache = new QueryCache(10);
        productService = new ProductServiceImpl(productRepository, queryCache);
        clearProducts();
    }

    private void tearDown() throws Exception {
        TestBase.tearDownAll();
    }

    private void testCreateProduct() {
        System.out.println("Running testCreateProduct...");

        Product product = productService.createProduct("Test Product", "Electronics", "TestBrand", 99.99, "Test description", testUserId);

        assertNotNull(product, "Created product should not be null");
        assertEquals("Test Product", product.getName(), "Name should match");
        assertEquals("Electronics", product.getCategory(), "Category should match");
        assertEquals(99.99, product.getPrice(), 0.001, "Price should match");

        Optional<Product> found = productService.getProductById(product.getId());
        assertTrue(found.isPresent(), "Created product should be findable");

        System.out.println("testCreateProduct PASSED");
    }

    private void testGetProductById() {
        System.out.println("Running testGetProductById...");

        Product product = productService.createProduct("Find Test", "Category", "Brand", 50.0, "Desc", testUserId);
        Optional<Product> found = productService.getProductById(product.getId());

        assertTrue(found.isPresent(), "Product should be found by ID");
        assertEquals(product.getId(), found.get().getId(), "IDs should match");

        Optional<Product> notFound = productService.getProductById(999999L);
        assertFalse(notFound.isPresent(), "Non-existent ID should return empty");

        System.out.println("testGetProductById PASSED");
    }

    private void testUpdateProduct() {
        System.out.println("Running testUpdateProduct...");

        Product product = productService.createProduct("Old Name", "Old Cat", "Old Brand", 10.0, "Old Desc", testUserId);
        boolean updated = productService.updateProduct(product.getId(), "New Name", "New Cat", "New Brand", 20.0, "New Desc");

        assertTrue(updated, "Update should return true");

        Optional<Product> updatedProduct = productService.getProductById(product.getId());
        assertTrue(updatedProduct.isPresent(), "Updated product should exist");
        assertEquals("New Name", updatedProduct.get().getName(), "Name should be updated");
        assertEquals("New Cat", updatedProduct.get().getCategory(), "Category should be updated");
        assertEquals(20.0, updatedProduct.get().getPrice(), 0.001, "Price should be updated");

        System.out.println("testUpdateProduct PASSED");
    }

    private void testDeleteProduct() {
        System.out.println("Running testDeleteProduct...");

        Product product = productService.createProduct("To Delete", "Category", "Brand", 10.0, "Desc", testUserId);
        boolean deleted = productService.deleteProduct(product.getId());

        assertTrue(deleted, "Delete should return true");

        Optional<Product> found = productService.getProductById(product.getId());
        assertFalse(found.isPresent(), "Deleted product should not exist");

        System.out.println("testDeleteProduct PASSED");
    }

    private void testSearchProducts() {
        System.out.println("Running testSearchProducts...");

        productService.createProduct("iPhone 15", "Electronics", "Apple", 999.99, "Smartphone", testUserId);
        productService.createProduct("MacBook Pro", "Electronics", "Apple", 1999.99, "Laptop", testUserId);
        productService.createProduct("Galaxy S24", "Electronics", "Samsung", 899.99, "Smartphone", testUserId);

        // Test search with caching
        List<Product> appleProducts = productService.searchProducts("iphone", null, null, null, null);
        assertTrue(appleProducts.size() >= 1, "Should find products by name substring");

        List<Product> electronics = productService.searchProducts(null, "Electronics", null, null, null);
        assertTrue(electronics.size() >= 3, "Should find products by category");

        List<Product> appleBrand = productService.searchProducts(null, null, "Apple", null, null);
        assertTrue(appleBrand.size() >= 2, "Should find products by brand");

        List<Product> cheapProducts = productService.searchProducts(null, null, null, 0.0, 1000.0);
        assertTrue(cheapProducts.size() >= 2, "Should find products by price range");

        System.out.println("testSearchProducts PASSED");
    }

    private void testGetAllProducts() {
        System.out.println("Running testGetAllProducts...");

        productService.createProduct("Product 1", "Cat1", "Brand1", 10.0, "Desc1", testUserId);
        productService.createProduct("Product 2", "Cat2", "Brand2", 20.0, "Desc2", testUserId);

        List<Product> products = productService.getAllProducts();
        assertTrue(products.size() >= 2, "Should find all products");

        System.out.println("testGetAllProducts PASSED - found " + products.size() + " products");
    }

    private void testGetTotalProductsCount() {
        System.out.println("Running testGetTotalProductsCount...");

        int initialCount = productService.getTotalProductsCount();

        productService.createProduct("Count Test 1", "Category", "Brand", 10.0, "Desc", testUserId);
        productService.createProduct("Count Test 2", "Category", "Brand", 20.0, "Desc", testUserId);

        int finalCount = productService.getTotalProductsCount();
        assertTrue(finalCount >= initialCount + 2, "Count should increase after creating products");

        System.out.println("testGetTotalProductsCount PASSED - count: " + finalCount);
    }

    private void testCacheInvalidation() {
        System.out.println("Running testCacheInvalidation...");

        // First search - should cache
        productService.createProduct("Cache Test", "Electronics", "Brand", 100.0, "Desc", testUserId);
        List<Product> firstSearch = productService.searchProducts("cache", null, null, null, null);

        // Create new product - should invalidate cache
        productService.createProduct("Cache Test 2", "Electronics", "Brand", 200.0, "Desc", testUserId);

        // Second search - should not use cache due to invalidation
        List<Product> secondSearch = productService.searchProducts("cache", null, null, null, null);
        assertTrue(secondSearch.size() >= 2, "Should find both products after cache invalidation");

        System.out.println("testCacheInvalidation PASSED");
    }

    // Assertion methods
    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    protected void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    protected void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("FAIL: " + message);
        }
    }
}