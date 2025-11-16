package repository.product;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория товаров
 */
public class ProductRepositoryImpl implements ProductRepository {
    private final Connection connection;

    public ProductRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Product create(String name, String category, String brand, double price, String description, Long userId) {
        String sql = "INSERT INTO app_schema.products (name, category, brand, price, description, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, brand);
            stmt.setDouble(4, price);
            stmt.setString(5, description);
            stmt.setLong(6, userId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newId = generatedKeys.getLong(1);
                        return findById(newId).orElseThrow(() ->
                                new RuntimeException("Failed to retrieve created product"));
                    }
                }
            }
            throw new RuntimeException("Failed to create product");

        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating product", e);
        }
    }

    @Override
    public boolean update(long id, String name, String category, String brand, Double price, String description) {
        String sql = "UPDATE app_schema.products SET name = ?, category = ?, brand = ?, price = ?, description = ?, updated_at = NOW() WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, brand);
            stmt.setDouble(4, price);
            stmt.setString(5, description);
            stmt.setLong(6, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating product", e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM app_schema.products WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            throw new RuntimeException("Database error while finding product by ID", e);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM app_schema.products ORDER BY id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
            throw new RuntimeException("Database error while finding all products", e);
        }

        return products;
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM app_schema.products WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while deleting product", e);
        }
    }

    @Override
    public boolean updatePartial(long id, String name, String category, String brand, Double price, String description) {
        StringBuilder sql = new StringBuilder("UPDATE app_schema.products SET ");
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

        sql.append("updated_at = NOW() WHERE id = ?");
        params.add(id);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while partially updating product", e);
        }
    }

    @Override
    public List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, name, category, brand, price, description, created_at, updated_at " +
                        "FROM app_schema.products WHERE 1=1"
        );
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
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
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
            throw new RuntimeException("Database error while searching products", e);
        }

        return products;
    }

    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM app_schema.products";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while counting products", e);
        }
    }

    private void setProductTimestamps(Product product, ResultSet rs) throws SQLException {
        try {
            java.lang.reflect.Field createdAtField = Product.class.getDeclaredField("createdAt");
            java.lang.reflect.Field updatedAtField = Product.class.getDeclaredField("updatedAt");

            createdAtField.setAccessible(true);
            updatedAtField.setAccessible(true);

            createdAtField.set(product, rs.getTimestamp("created_at"));
            updatedAtField.set(product, rs.getTimestamp("updated_at"));

        } catch (Exception e) {
            throw new RuntimeException("Cannot set product timestamps", e);
        }
    }
}