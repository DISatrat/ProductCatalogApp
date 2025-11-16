package test.main.repository;

import model.Product;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class ProductRepositoryTest extends TestBase {

    private ProductRepository repository;

    public static void main(String[] args) {
        try {
            ProductRepositoryTest test = new ProductRepositoryTest();
            test.runAllTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllTests() throws Exception {
        setUp();

        testCreateProduct();
        testFindById();
        testFindAll();
        testUpdateProduct();
        testUpdatePartial();
        testDeleteProduct();
        testSearch();
        testGetCount();

        tearDown();
        System.out.println("All ProductRepository tests passed!");
    }

    private void setUp() throws Exception {
        TestBase.setUpAll();
        repository = new ProductRepositoryImpl(connection);
        clearProducts();
    }

    private void tearDown() throws Exception {
        TestBase.tearDownAll();
    }

    private void testCreateProduct() {
        System.out.println("Running testCreateProduct...");

        Product product = repository.create("Test Product", "Electronics", "TestBrand", 99.99, "Test description", testUserId);

        assertNotNull(product, "Created product should not be null");
        assertEquals("Test Product", product.getName(), "Name should match");
        assertEquals("Electronics", product.getCategory(), "Category should match");
        assertEquals(99.99, product.getPrice(), 0.001, "Price should match");

        Optional<Product> found = repository.findById(product.getId());
        assertTrue(found.isPresent(), "Created product should be findable");

        System.out.println("testCreateProduct PASSED");
    }

    private void testFindById() {
        System.out.println("Running testFindById...");

        Product product = repository.create("Find Test", "Category", "Brand", 50.0, "Desc", testUserId);
        Optional<Product> found = repository.findById(product.getId());

        assertTrue(found.isPresent(), "Product should be found by ID");
        assertEquals(product.getId(), found.get().getId(), "IDs should match");

        Optional<Product> notFound = repository.findById(999999L);
        assertFalse(notFound.isPresent(), "Non-existent ID should return empty");

        System.out.println("testFindById PASSED");
    }

    private void testFindAll() {
        System.out.println("Running testFindAll...");

        repository.create("Product 1", "Cat1", "Brand1", 10.0, "Desc1", testUserId);
        repository.create("Product 2", "Cat2", "Brand2", 20.0, "Desc2", testUserId);

        List<Product> products = repository.findAll();
        assertTrue(products.size() >= 2, "Should find all products");

        System.out.println("testFindAll PASSED - found " + products.size() + " products");
    }

    private void testUpdateProduct() {
        System.out.println("Running testUpdateProduct...");

        Product product = repository.create("Old Name", "Old Cat", "Old Brand", 10.0, "Old Desc", testUserId);
        boolean updated = repository.update(product.getId(), "New Name", "New Cat", "New Brand", 20.0, "New Desc");

        assertTrue(updated, "Update should return true");

        Optional<Product> updatedProduct = repository.findById(product.getId());
        assertTrue(updatedProduct.isPresent(), "Updated product should exist");
        assertEquals("New Name", updatedProduct.get().getName(), "Name should be updated");
        assertEquals("New Cat", updatedProduct.get().getCategory(), "Category should be updated");
        assertEquals(20.0, updatedProduct.get().getPrice(), 0.001, "Price should be updated");

        System.out.println("testUpdateProduct PASSED");
    }

    private void testUpdatePartial() {
        System.out.println("Running testUpdatePartial...");

        Product product = repository.create("Partial Test", "Category", "Brand", 10.0, "Desc", testUserId);
        boolean updated = repository.updatePartial(product.getId(), "New Name", null, null, null, null);

        assertTrue(updated, "Partial update should return true");

        Optional<Product> updatedProduct = repository.findById(product.getId());
        assertTrue(updatedProduct.isPresent(), "Updated product should exist");
        assertEquals("New Name", updatedProduct.get().getName(), "Name should be updated");
        assertEquals("Category", updatedProduct.get().getCategory(), "Category should remain unchanged");
        assertEquals(10.0, updatedProduct.get().getPrice(), 0.001, "Price should remain unchanged");

        System.out.println("testUpdatePartial PASSED");
    }

    private void testDeleteProduct() {
        System.out.println("Running testDeleteProduct...");

        Product product = repository.create("To Delete", "Category", "Brand", 10.0, "Desc", testUserId);
        boolean deleted = repository.delete(product.getId());

        assertTrue(deleted, "Delete should return true");

        Optional<Product> found = repository.findById(product.getId());
        assertFalse(found.isPresent(), "Deleted product should not exist");

        System.out.println("testDeleteProduct PASSED");
    }

    private void testSearch() {
        System.out.println("Running testSearch...");

        repository.create("iPhone 15", "Electronics", "Apple", 999.99, "Smartphone", testUserId);
        repository.create("MacBook Pro", "Electronics", "Apple", 1999.99, "Laptop", testUserId);
        repository.create("Galaxy S24", "Electronics", "Samsung", 899.99, "Smartphone", testUserId);
        repository.create("ThinkPad", "Electronics", "Lenovo", 1299.99, "Laptop", testUserId);

        List<Product> appleProducts = repository.search("iphone", null, null, null, null);
        assertTrue(appleProducts.size() >= 1, "Should find products by name substring");

        List<Product> electronics = repository.search(null, "Electronics", null, null, null);
        assertTrue(electronics.size() >= 4, "Should find products by category");

        List<Product> appleBrand = repository.search(null, null, "Apple", null, null);
        assertTrue(appleBrand.size() >= 2, "Should find products by brand");

        List<Product> cheapProducts = repository.search(null, null, null, 0.0, 1000.0);
        assertTrue(cheapProducts.size() >= 2, "Should find products by price range");

        List<Product> appleLaptops = repository.search(null, "Electronics", "Apple", 1500.0, 2500.0);
        assertTrue(appleLaptops.size() >= 1, "Should find products by combined criteria");

        System.out.println("testSearch PASSED");
    }

    private void testGetCount() {
        System.out.println("Running testGetCount...");

        int initialCount = repository.getCount();

        repository.create("Count Test 1", "Category", "Brand", 10.0, "Desc", testUserId);
        repository.create("Count Test 2", "Category", "Brand", 20.0, "Desc", testUserId);

        int finalCount = repository.getCount();
        assertTrue(finalCount >= initialCount + 2, "Count should increase after creating products");

        System.out.println("testGetCount PASSED - count: " + finalCount);
    }

    // Assertion methods
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    public void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("FAIL: " + message);
        }
    }

    public void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("FAIL: " + message);
        }
    }
}
