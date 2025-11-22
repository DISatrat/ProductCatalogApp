package test.main.service;

import cache.QueryCache;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.product.ProductRepository;
import service.product.ProductService;
import service.product.ProductServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private QueryCache queryCache;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, queryCache);
    }

    @Test
    void testCreateProduct() {
        String name = "Test Product";
        String category = "Electronics";
        String brand = "TestBrand";
        double price = 99.99;
        String description = "Test description";
        Long userId = 1L;

        Product expectedProduct = createProduct(1L, name, category, brand, price, description);
        when(productRepository.create(name, category, brand, price, description, userId))
                .thenReturn(expectedProduct);
        doNothing().when(queryCache).invalidateAll();

        Product result = productService.createProduct(name, category, brand, price, description, userId);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(category, result.getCategory());
        assertEquals(brand, result.getBrand());
        assertEquals(price, result.getPrice(), 0.001);
        assertEquals(description, result.getDescription());

        verify(productRepository, times(1)).create(name, category, brand, price, description, userId);
        verify(queryCache, times(1)).invalidateAll();
    }

    @Test
    void testGetProductById_Found() {
        Long productId = 1L;
        Product expectedProduct = createProduct(productId, "Test Product", "Electronics", "Brand", 99.99, "Description");
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        Optional<Product> result = productService.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(expectedProduct, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(productId);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        String newName = "New Name";
        String newCategory = "New Category";
        String newBrand = "New Brand";
        Double newPrice = 20.0;
        String newDescription = "New Description";

        Product updatedProduct = createProduct(productId, newName, newCategory, newBrand, newPrice, newDescription);
        when(productRepository.update(productId, newName, newCategory, newBrand, newPrice, newDescription))
                .thenReturn(updatedProduct);
        doNothing().when(queryCache).invalidateAll();

        Product result = productService.updateProduct(productId, newName, newCategory, newBrand, newPrice, newDescription);

        assertNotNull(result);
        assertEquals(newName, result.getName());
        assertEquals(newCategory, result.getCategory());
        assertEquals(newBrand, result.getBrand());
        assertEquals(newPrice, result.getPrice(), 0.001);

        verify(productRepository, times(1)).update(productId, newName, newCategory, newBrand, newPrice, newDescription);
        verify(queryCache, times(1)).invalidateAll();
    }

    @Test
    void testDeleteProduct() {
        Long productId = 1L;
        when(productRepository.delete(productId)).thenReturn(true);
        doNothing().when(queryCache).invalidateAll();

        boolean result = productService.deleteProduct(productId);

        assertTrue(result);
        verify(productRepository, times(1)).delete(productId);
        verify(queryCache, times(1)).invalidateAll();
    }

    @Test
    void testDeleteProduct_NotFound() {
        Long productId = 999L;
        when(productRepository.delete(productId)).thenReturn(false);

        boolean result = productService.deleteProduct(productId);

        assertFalse(result);
        verify(productRepository, times(1)).delete(productId);
        verify(queryCache, never()).invalidateAll();
    }

    @Test
    void testSearchProducts_WithCacheHit() {
        String nameSubstring = "iphone";
        String category = "Electronics";
        String brand = "Apple";
        Double minPrice = 0.0;
        Double maxPrice = 1000.0;

        String cacheKey = "search:iphone:Electronics:Apple:0.0:1000.0";
        List<Product> cachedProducts = Arrays.asList(
                createProduct(1L, "iPhone 15", "Electronics", "Apple", 999.99, "Smartphone")
        );

        when(queryCache.get(cacheKey)).thenReturn(cachedProducts);

        List<Product> result = productService.searchProducts(nameSubstring, category, brand, minPrice, maxPrice);


        assertEquals(cachedProducts, result);
        verify(queryCache, times(1)).get(cacheKey);
        verify(productRepository, never()).search(any(), any(), any(), any(), any());
        verify(queryCache, never()).put(any(), any());
    }

    @Test
    void testSearchProducts_WithCacheMiss() {
        String nameSubstring = "iphone";
        String category = "Electronics";
        String brand = "Apple";
        Double minPrice = 0.0;
        Double maxPrice = 1000.0;

        String cacheKey = "search:iphone:Electronics:Apple:0.0:1000.0";
        List<Product> dbProducts = Arrays.asList(
                createProduct(1L, "iPhone 15", "Electronics", "Apple", 999.99, "Smartphone")
        );

        when(queryCache.get(cacheKey)).thenReturn(null);
        when(productRepository.search(nameSubstring, category, brand, minPrice, maxPrice)).thenReturn(dbProducts);
        doNothing().when(queryCache).put(cacheKey, dbProducts);

        List<Product> result = productService.searchProducts(nameSubstring, category, brand, minPrice, maxPrice);

        assertEquals(dbProducts, result);
        verify(queryCache, times(1)).get(cacheKey);
        verify(productRepository, times(1)).search(nameSubstring, category, brand, minPrice, maxPrice);
        verify(queryCache, times(1)).put(cacheKey, dbProducts);
    }

    @Test
    void testGetAllProducts() {
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1L, "Product 1", "Cat1", "Brand1", 10.0, "Desc1"),
                createProduct(2L, "Product 2", "Cat2", "Brand2", 20.0, "Desc2")
        );
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> result = productService.getAllProducts();

        assertEquals(expectedProducts, result);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetTotalProductsCount() {
        int expectedCount = 5;
        when(productRepository.getCount()).thenReturn(expectedCount);

        int result = productService.getTotalProductsCount();

        assertEquals(expectedCount, result);
        verify(productRepository, times(1)).getCount();
    }

    @Test
    void testCacheInvalidationOnCreate() {
        Product newProduct = createProduct(1L, "New Product", "Electronics", "Brand", 100.0, "Desc");
        when(productRepository.create(any(), any(), any(), anyDouble(), any(), any())).thenReturn(newProduct);
        doNothing().when(queryCache).invalidateAll();

        productService.createProduct("New Product", "Electronics", "Brand", 100.0, "Desc", 1L);

        verify(queryCache, times(1)).invalidateAll();
    }

    @Test
    void testCacheInvalidationOnUpdate() {
        Product updatedProduct = createProduct(1L, "Updated Product", "Electronics", "Brand", 150.0, "Desc");
        when(productRepository.update(any(), any(), any(), any(), any(), any())).thenReturn(updatedProduct);
        doNothing().when(queryCache).invalidateAll();

        productService.updateProduct(1L, "Updated Product", "Electronics", "Brand", 150.0, "Desc");

        verify(queryCache, times(1)).invalidateAll();
    }

    private Product createProduct(Long id, String name, String category, String brand, double price, String description) {
        Product product = new Product(id, name, category, brand, price, description);
        return product;
    }
}
