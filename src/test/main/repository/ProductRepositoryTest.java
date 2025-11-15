package test.main.repository;

import main.model.Product;
import main.repository.ProductRepository;
import test.TestBase;

import java.util.List;
import java.util.Optional;

public class ProductRepositoryTest extends TestBase {

    private ProductRepository repository;

    public static void main(String[] args) {
        ProductRepositoryTest test = new ProductRepositoryTest();
        test.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== Running ProductRepository Tests ===");

        testCreateProduct();
        testFindByIdWhenProductExists();
        testFindByIdWhenProductNotExists();
        testFindAllProducts();
        testDeleteProduct();
        testUpdateProduct();
        testUpdatePartialProduct();
        testSearchProductsByName();
        testSearchProductsByCategory();
        testSearchProductsByBrand();
        testSearchProductsByPriceRange();
        testSearchProductsWithMultipleFilters();
        testGetCount();
        testRestoreIdGenerator();

        System.out.println("=== All ProductRepository tests passed! ===");
    }

    private void setUp() {
        repository = new ProductRepository();
    }

    private Product createTestProduct() {
        return repository.create("Test Product", "Electronics", "TestBrand", 99.99, "Test description");
    }

    public void testCreateProduct() {
        setUp();

        Product product = createTestProduct();

        assertNotNull(product, "Created product should not be null");
        assertEquals("Test Product", product.getName(), "Name should match");
        assertEquals("Electronics", product.getCategory(), "Category should match");
        assertEquals(99.99, product.getPrice(), 0.001, "Price should match");

        Optional<Product> found = repository.findById(product.getId());
        assertTrue(found.isPresent(), "Created product should be findable");
        System.out.println("✓ testCreateProduct passed");
    }

    public void testFindByIdWhenProductExists() {
        setUp();
        Product product = createTestProduct();

        Optional<Product> found = repository.findById(product.getId());

        assertTrue(found.isPresent(), "Product should be found");
        assertEquals(product.getId(), found.get().getId(), "IDs should match");
        System.out.println("✓ testFindByIdWhenProductExists passed");
    }

    public void testFindByIdWhenProductNotExists() {
        setUp();

        Optional<Product> found = repository.findById(999L);

        assertFalse(found.isPresent(), "Product should not be found");
        System.out.println("✓ testFindByIdWhenProductNotExists passed");
    }

    public void testFindAllProducts() {
        setUp();
        Product product1 = createTestProduct();
        Product product2 = repository.create("Product 2", "Books", "Brand2", 29.99, "Description 2");

        List<Product> allProducts = repository.findAll();

        assertEquals(2, allProducts.size(), "Should return all products");
        assertTrue(allProducts.stream().anyMatch(p -> p.getId() == product1.getId()), "Should contain first product");
        assertTrue(allProducts.stream().anyMatch(p -> p.getId() == product2.getId()), "Should contain second product");
        System.out.println("✓ testFindAllProducts passed");
    }

    public void testDeleteProduct() {
        setUp();
        Product product = createTestProduct();

        assertTrue(repository.delete(product.getId()), "Delete should return true for existing product");
        assertFalse(repository.findById(product.getId()).isPresent(), "Product should no longer exist");
        assertFalse(repository.delete(999L), "Delete should return false for non-existing product");
        System.out.println("✓ testDeleteProduct passed");
    }

    public void testUpdateProduct() {
        setUp();
        Product product = createTestProduct();

        boolean result = repository.update(product.getId(), "Updated Name", "Updated Category",
                "Updated Brand", 199.99, "Updated description");

        assertTrue(result, "Update should succeed");
        Optional<Product> updated = repository.findById(product.getId());
        assertTrue(updated.isPresent(), "Product should still exist");
        assertEquals("Updated Name", updated.get().getName(), "Name should be updated");
        assertEquals("Updated Category", updated.get().getCategory(), "Category should be updated");
        assertEquals("Updated Brand", updated.get().getBrand(), "Brand should be updated");
        assertEquals(199.99, updated.get().getPrice(), 0.001, "Price should be updated");
        System.out.println("✓ testUpdateProduct passed");
    }

    public void testUpdatePartialProduct() {
        setUp();
        Product product = createTestProduct();
        String originalName = product.getName();
        String originalCategory = product.getCategory();

        boolean result = repository.updatePartial(product.getId(), null, null, null, 149.99, "New description");

        assertTrue(result, "Partial update should succeed");
        Optional<Product> updated = repository.findById(product.getId());
        assertTrue(updated.isPresent(), "Product should exist");
        assertEquals(originalName, updated.get().getName(), "Name should not change");
        assertEquals(originalCategory, updated.get().getCategory(), "Category should not change");
        assertEquals(149.99, updated.get().getPrice(), 0.001, "Price should be updated");
        assertEquals("New description", updated.get().getDescription(), "Description should be updated");
        System.out.println("✓ testUpdatePartialProduct passed");
    }

    public void testSearchProductsByName() {
        setUp();
        repository.create("Apple iPhone", "Electronics", "Apple", 999.99, "Smartphone");
        repository.create("Samsung Galaxy", "Electronics", "Samsung", 899.99, "Android phone");
        repository.create("MacBook Pro", "Computers", "Apple", 1999.99, "Laptop");

        List<Product> results = repository.search("apple", null, null, null, null);

        assertEquals(2, results.size(), "Should find products containing 'apple'");
        assertTrue(results.stream().allMatch(p -> p.getName().toLowerCase().contains("apple")),
                "All results should contain 'apple' in name");
        System.out.println("✓ testSearchProductsByName passed");
    }

    public void testSearchProductsByCategory() {
        setUp();
        repository.create("Product1", "Electronics", "Brand1", 100.0, "Desc1");
        repository.create("Product2", "Books", "Brand2", 50.0, "Desc2");
        repository.create("Product3", "Electronics", "Brand3", 200.0, "Desc3");

        List<Product> results = repository.search(null, "Electronics", null, null, null);

        assertEquals(2, results.size(), "Should find 2 electronics products");
        assertTrue(results.stream().allMatch(p -> p.getCategory().equalsIgnoreCase("Electronics")),
                "All results should be in Electronics category");
        System.out.println("✓ testSearchProductsByCategory passed");
    }

    public void testSearchProductsByBrand() {
        setUp();
        repository.create("Product1", "Category1", "Apple", 100.0, "Desc1");
        repository.create("Product2", "Category2", "Samsung", 150.0, "Desc2");
        repository.create("Product3", "Category3", "Apple", 200.0, "Desc3");

        List<Product> results = repository.search(null, null, "Apple", null, null);

        assertEquals(2, results.size(), "Should find 2 Apple products");
        assertTrue(results.stream().allMatch(p -> p.getBrand().equalsIgnoreCase("Apple")),
                "All results should be Apple brand");
        System.out.println("✓ testSearchProductsByBrand passed");
    }

    public void testSearchProductsByPriceRange() {
        setUp();
        repository.create("Cheap", "Category1", "Brand1", 50.0, "Desc1");
        repository.create("Medium", "Category2", "Brand2", 150.0, "Desc2");
        repository.create("Expensive", "Category3", "Brand3", 250.0, "Desc3");

        List<Product> results = repository.search(null, null, null, 100.0, 200.0);

        assertEquals(1, results.size(), "Should find 1 product in price range");
        assertEquals("Medium", results.get(0).getName(), "Should find Medium product");
        assertTrue(results.get(0).getPrice() >= 100.0 && results.get(0).getPrice() <= 200.0,
                "Price should be in range");
        System.out.println("✓ testSearchProductsByPriceRange passed");
    }

    public void testSearchProductsWithMultipleFilters() {
        setUp();
        repository.create("iPhone 13", "Electronics", "Apple", 999.0, "Smartphone");
        repository.create("iPhone 12", "Electronics", "Apple", 799.0, "Smartphone");
        repository.create("Galaxy S21", "Electronics", "Samsung", 899.0, "Smartphone");
        repository.create("MacBook Air", "Computers", "Apple", 1299.0, "Laptop");

        List<Product> results = repository.search("iPhone", "Electronics", "Apple", 800.0, 1000.0);

        assertEquals(1, results.size(), "Should find exactly one matching product");
        assertEquals("iPhone 13", results.get(0).getName(), "Should find iPhone 13");
        System.out.println("✓ testSearchProductsWithMultipleFilters passed");
    }

    public void testGetCount() {
        setUp();

        assertEquals(0, repository.getCount(), "Initial count should be 0");

        createTestProduct();
        createTestProduct();

        assertEquals(2, repository.getCount(), "Count should be 2 after adding products");
        System.out.println("✓ testGetCount passed");
    }

    public void testRestoreIdGenerator() {
        setUp();

        Product p1 = createTestProduct();
        Product p2 = createTestProduct();

        repository.restoreIdGenerator();

        Product p3 = repository.create("New Product", "Category", "Brand", 100.0, "Description");

        assertEquals(3L, p3.getId(), "New product should have ID 3");
        System.out.println("✓ testRestoreIdGenerator passed");
    }
}