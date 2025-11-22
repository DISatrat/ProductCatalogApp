package repository.product;

import exception.EntityNotFoundException;
import exception.ProductRepositoryException;
import model.Product;
import util.ConnectionPoolManager;
import util.SQLConstants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория товаров
 */
/**
 * Реализация репозитория товаров
 */
public class ProductRepositoryImpl implements ProductRepository {
    // Убираем поле connection

    public ProductRepositoryImpl() {
        // Конструктор без параметров
    }

    @Override
    public Product create(String name, String category, String brand, double price, String description, Long userId) {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, brand);
            stmt.setDouble(4, price);
            stmt.setString(5, description);
            stmt.setLong(6, userId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new ProductRepositoryException("Failed to create product, no rows affected");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long newId = generatedKeys.getLong(1);
                    return findById(newId).orElseThrow(() ->
                            new EntityNotFoundException("Failed to retrieve created product with ID: " + newId));
                } else {
                    throw new ProductRepositoryException("Failed to retrieve generated product ID");
                }
            }

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while creating product", e);
        }
    }

    @Override
    public Product update(long id, String name, String category, String brand, Double price, String description) {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.UPDATE)) {

            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, brand);
            stmt.setDouble(4, price);
            stmt.setString(5, description);
            stmt.setLong(6, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new EntityNotFoundException("Product not found with ID: " + id);
            }

            return findById(id)
                    .orElseThrow(() -> new ProductRepositoryException("Failed to retrieve updated product with ID: " + id));

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while updating product with ID: " + id, e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.SELECT_BY_ID)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                setProductTimestamps(product, rs);
                return Optional.of(product);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while finding product by ID: " + id, e);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                setProductTimestamps(product, rs);
                products.add(product);
            }

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while retrieving all products", e);
        }

        return products;
    }

    @Override
    public boolean delete(long id) {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.DELETE)) {

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while deleting product with ID: " + id, e);
        }
    }

    @Override
    public boolean updatePartial(long id, String name, String category, String brand, Double price, String description) {
        StringBuilder sql = new StringBuilder("UPDATE " + SQLConstants.Product.TABLE + " SET ");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append("name = ?, ");
            params.add(name);
        }
        if (category != null) {
            sql.append("category = ?, ");
            params.add(category);
        }
        if (brand != null) {
            sql.append("brand = ?, ");
            params.add(brand);
        }
        if (price != null) {
            sql.append("price = ?, ");
            params.add(price);
        }
        if (description != null) {
            sql.append("description = ?, ");
            params.add(description);
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields to update for product with ID: " + id);
        }

        sql.append("updated_at = NOW() WHERE id = ?");
        params.add(id);

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while partially updating product with ID: " + id, e);
        }
    }

    @Override
    public List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice) {
        StringBuilder sql = new StringBuilder(SQLConstants.Product.BASE_SEARCH);
        List<Object> params = new ArrayList<>();

        if (nameSubstring != null && !nameSubstring.isEmpty()) {
            sql.append(" AND LOWER(name) LIKE LOWER(?)");
            params.add("%" + nameSubstring + "%");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND LOWER(category) = LOWER(?)");
            params.add(category);
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND LOWER(brand) = LOWER(?)");
            params.add(brand);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        sql.append(" ORDER BY id");
        List<Product> products = new ArrayList<>();

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                setProductTimestamps(product, rs);
                products.add(product);
            }

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while searching products", e);
        }

        return products;
    }

    @Override
    public int getCount() {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQLConstants.Product.COUNT);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new ProductRepositoryException("Database error while counting products", e);
        }
    }

    /**
     * Устанавливает временные метки продукта через рефлексию
     */
    private void setProductTimestamps(Product product, ResultSet rs) {
        try {
            java.lang.reflect.Field createdAtField = Product.class.getDeclaredField("createdAt");
            java.lang.reflect.Field updatedAtField = Product.class.getDeclaredField("updatedAt");

            createdAtField.setAccessible(true);
            updatedAtField.setAccessible(true);

            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp updatedAt = rs.getTimestamp("updated_at");

            if (!rs.wasNull()) {
                createdAtField.set(product, createdAt);
                updatedAtField.set(product, updatedAt);
            }

        } catch (NoSuchFieldException e) {
            throw new ProductRepositoryException("Product class does not have timestamp fields", e);
        } catch (IllegalAccessException e) {
            throw new ProductRepositoryException("Cannot access timestamp fields in Product class", e);
        } catch (SQLException e) {
            throw new ProductRepositoryException("Error reading timestamps from ResultSet", e);
        }
    }
}