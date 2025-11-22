package test.main.repository;

import exception.EntityNotFoundException;
import exception.ProductRepositoryException;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import util.SQLConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSet generatedKeys;

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepositoryImpl();
    }

    @Test
    void testCreate_ShouldInsertProductAndReturnWithGeneratedId() throws SQLException {
        String name = "Test Product";
        String category = "Electronics";
        String brand = "TestBrand";
        double price = 99.99;
        String description = "Test description";
        Long userId = 1L;

        when(connection.prepareStatement(SQLConstants.Product.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getLong(1)).thenReturn(123L);

        when(connection.prepareStatement(SQLConstants.Product.SELECT_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(123L);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("category")).thenReturn(category);
        when(resultSet.getString("brand")).thenReturn(brand);
        when(resultSet.getDouble("price")).thenReturn(price);
        when(resultSet.getString("description")).thenReturn(description);
        when(resultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        Product product = productRepository.create(name, category, brand, price, description, userId);

        assertNotNull(product);
        assertEquals(123L, product.getId());
        assertEquals(name, product.getName());
        assertEquals(category, product.getCategory());
        assertEquals(brand, product.getBrand());
        assertEquals(price, product.getPrice(), 0.001);

        verify(preparedStatement).setString(1, name);
        verify(preparedStatement).setString(2, category);
        verify(preparedStatement).setString(3, brand);
        verify(preparedStatement).setDouble(4, price);
        verify(preparedStatement).setString(5, description);
        verify(preparedStatement).setLong(6, userId);
        verify(preparedStatement).executeUpdate();
        verify(generatedKeys).next();
        verify(generatedKeys).getLong(1);
    }

    @Test
    void testCreate_ShouldThrowExceptionWhenNoRowsAffected() throws SQLException {
        when(connection.prepareStatement(SQLConstants.Product.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(ProductRepositoryException.class, () -> {
            productRepository.create("Test", "Category", "Brand", 10.0, "Desc", 1L);
        });
    }

    @Test
    void testCreate_ShouldThrowExceptionWhenNoGeneratedKey() throws SQLException {
        when(connection.prepareStatement(SQLConstants.Product.INSERT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        assertThrows(ProductRepositoryException.class, () -> {
            productRepository.create("Test", "Category", "Brand", 10.0, "Desc", 1L);
        });
    }

    @Test
    void testFindById_ExistingProduct_ShouldReturnProduct() throws SQLException {
        Long productId = 1L;
        when(connection.prepareStatement(SQLConstants.Product.SELECT_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(productId);
        when(resultSet.getString("name")).thenReturn("Test Product");
        when(resultSet.getString("category")).thenReturn("Electronics");
        when(resultSet.getString("brand")).thenReturn("Brand");
        when(resultSet.getDouble("price")).thenReturn(99.99);
        when(resultSet.getString("description")).thenReturn("Description");
        when(resultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        Optional<Product> result = productRepository.findById(productId);

        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
        verify(preparedStatement).setLong(1, productId);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testFindById_NonExistentProduct_ShouldReturnEmpty() throws SQLException {
        long productId = 999L;
        when(connection.prepareStatement(SQLConstants.Product.SELECT_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Optional<Product> result = productRepository.findById(productId);

        assertFalse(result.isPresent());
        verify(preparedStatement).setLong(1, productId);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testUpdate_ShouldExecuteUpdateAndReturnProduct() throws SQLException {
        Long productId = 1L;
        String name = "Updated Name";
        String category = "Updated Category";
        String brand = "Updated Brand";
        Double price = 20.0;
        String description = "Updated Description";

        when(connection.prepareStatement(SQLConstants.Product.UPDATE)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        when(connection.prepareStatement(SQLConstants.Product.SELECT_BY_ID)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(productId);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("category")).thenReturn(category);
        when(resultSet.getString("brand")).thenReturn(brand);
        when(resultSet.getDouble("price")).thenReturn(price);
        when(resultSet.getString("description")).thenReturn(description);
        when(resultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        Product result = productRepository.update(productId, name, category, brand, price, description);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(preparedStatement).setString(1, name);
        verify(preparedStatement).setString(2, category);
        verify(preparedStatement).setString(3, brand);
        verify(preparedStatement).setDouble(4, price);
        verify(preparedStatement).setString(5, description);
        verify(preparedStatement).setLong(6, productId);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdate_ShouldThrowExceptionWhenProductNotFound() throws SQLException {
        Long productId = 999L;
        when(connection.prepareStatement(SQLConstants.Product.UPDATE)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> {
            productRepository.update(productId, "Name", "Category", "Brand", 10.0, "Description");
        });
    }

    @Test
    void testDelete_ExistingProduct_ShouldReturnTrue() throws SQLException {
        long productId = 1L;
        when(connection.prepareStatement(SQLConstants.Product.DELETE)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = productRepository.delete(productId);

        assertTrue(result);
        verify(preparedStatement).setLong(1, productId);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDelete_NonExistentProduct_ShouldReturnFalse() throws SQLException {
        Long productId = 999L;
        when(connection.prepareStatement(SQLConstants.Product.DELETE)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean result = productRepository.delete(productId);

        assertFalse(result);
        verify(preparedStatement).setLong(1, productId);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testSearch_WithParameters_ShouldBuildDynamicQuery() throws SQLException {
        String nameSubstring = "test";
        String category = "Electronics";
        String brand = "Brand";
        Double minPrice = 10.0;
        Double maxPrice = 100.0;

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        List<Product> result = productRepository.search(nameSubstring, category, brand, minPrice, maxPrice);

        assertNotNull(result);
        verify(connection).prepareStatement(contains("LOWER(name) LIKE LOWER(?)"));
        verify(connection).prepareStatement(contains("LOWER(category) = LOWER(?)"));
        verify(connection).prepareStatement(contains("LOWER(brand) = LOWER(?)"));
        verify(connection).prepareStatement(contains("price >= ?"));
        verify(connection).prepareStatement(contains("price <= ?"));
        verify(preparedStatement).setString(anyInt(), eq("%test%"));
        verify(preparedStatement).setString(anyInt(), eq("Electronics"));
        verify(preparedStatement).setString(anyInt(), eq("Brand"));
        verify(preparedStatement).setDouble(anyInt(), eq(10.0));
        verify(preparedStatement).setDouble(anyInt(), eq(100.0));
    }

    @Test
    void testGetCount_ShouldReturnCountFromDatabase() throws SQLException {
        when(connection.prepareStatement(SQLConstants.Product.COUNT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(42);

        int result = productRepository.getCount();

        assertEquals(42, result);
        verify(preparedStatement).executeQuery();
        verify(resultSet).next();
        verify(resultSet).getInt(1);
    }

    @Test
    void testFindAll_ShouldReturnAllProducts() throws SQLException {
        when(connection.prepareStatement(SQLConstants.Product.SELECT_ALL)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getLong("id")).thenReturn(1L, 2L);
        when(resultSet.getString("name")).thenReturn("Product1", "Product2");
        when(resultSet.getString("category")).thenReturn("Cat1", "Cat2");
        when(resultSet.getString("brand")).thenReturn("Brand1", "Brand2");
        when(resultSet.getDouble("price")).thenReturn(10.0, 20.0);
        when(resultSet.getString("description")).thenReturn("Desc1", "Desc2");
        when(resultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<Product> result = productRepository.findAll();

        assertEquals(2, result.size());
        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
    }

    @Test
    void testSQLException_ShouldWrapInRepositoryException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(ProductRepositoryException.class, () -> {
            productRepository.findById(1L);
        });
    }
}
