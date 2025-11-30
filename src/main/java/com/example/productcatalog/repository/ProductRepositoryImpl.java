package com.example.productcatalog.repository;

import com.example.productcatalog.exception.EntityNotFoundException;
import com.example.productcatalog.exception.RepositoryException;
import com.example.productcatalog.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация ProductRepository с использованием JDBC.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT =
            "INSERT INTO app_schema.products (name, category, brand, price, description, user_id) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE app_schema.products SET name = ?, category = ?, brand = ?, price = ?, description = ?, updated_at = NOW() WHERE id = ?";

    private static final String SELECT_BY_ID =
            "SELECT id, name, category, brand, price, description, user_id, created_at, updated_at FROM app_schema.products WHERE id = ?";

    private static final String SELECT_ALL =
            "SELECT id, name, category, brand, price, description, user_id, created_at, updated_at FROM app_schema.products ORDER BY id";

    private static final String DELETE_BY_ID =
            "DELETE FROM app_schema.products WHERE id = ?";

    private static final String COUNT =
            "SELECT COUNT(*) FROM app_schema.products";

    private static final String BASE_SEARCH =
            "SELECT id, name, category, brand, price, description, user_id, created_at, updated_at FROM app_schema.products WHERE 1=1";

    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> Product.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .category(rs.getString("category"))
            .brand(rs.getString("brand"))
            .price(rs.getDouble("price"))
            .description(rs.getString("description"))
            .userId(rs.getLong("user_id"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();

    @Override
    public Product save(Product product) {
        log.debug("Сохранение продукта: {}", product.getName());
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT, new String[]{"id"});
                ps.setString(1, product.getName());
                ps.setString(2, product.getCategory());
                ps.setString(3, product.getBrand());
                ps.setDouble(4, product.getPrice());
                ps.setString(5, product.getDescription());
                ps.setLong(6, product.getUserId());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                product.setId(keyHolder.getKey().longValue());
            }

            log.info("Продукт сохранен с идентификатором: {}", product.getId());
            return findById(product.getId()).orElseThrow(() ->
                    new EntityNotFoundException("Не удалось получить созданный продукт"));
        } catch (Exception e) {
            log.error("Ошибка сохранения продукта: {}", product.getName(), e);
            throw new RepositoryException("Ошибка сохранения продукта", e);
        }
    }

    @Override
    public Product update(Product product) {
        log.debug("Обновление продукта с идентификатором: {}", product.getId());
        try {
            int updated = jdbcTemplate.update(UPDATE,
                    product.getName(),
                    product.getCategory(),
                    product.getBrand(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getId());

            if (updated == 0) {
                throw new EntityNotFoundException("Продукт не найден с идентификатором: " + product.getId());
            }

            log.info("Продукт обновлен с идентификатором: {}", product.getId());
            return findById(product.getId()).orElseThrow(() ->
                    new EntityNotFoundException("Не удалось получить обновленный продукт"));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка обновления продукта: {}", product.getId(), e);
            throw new RepositoryException("Ошибка обновления продукта", e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        log.debug("Поиск продукта по идентификатору: {}", id);
        try {
            List<Product> products = jdbcTemplate.query(SELECT_BY_ID, productRowMapper, id);
            return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
        } catch (Exception e) {
            log.error("Ошибка поиска продукта по идентификатору: {}", id, e);
            throw new RepositoryException("Ошибка поиска продукта по идентификатору: " + id, e);
        }
    }

    @Override
    public List<Product> findAll() {
        log.debug("Поиск всех продуктов");
        try {
            return jdbcTemplate.query(SELECT_ALL, productRowMapper);
        } catch (Exception e) {
            log.error("Ошибка поиска всех продуктов", e);
            throw new RepositoryException("Ошибка поиска всех продуктов", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        log.debug("Удаление продукта по идентификатору: {}", id);
        try {
            int deleted = jdbcTemplate.update(DELETE_BY_ID, id);
            boolean result = deleted > 0;
            if (result) {
                log.info("Продукт удален с идентификатором: {}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления продукта: {}", id, e);
            throw new RepositoryException("Ошибка удаления продукта: " + id, e);
        }
    }

    @Override
    public List<Product> search(String nameSubstring, String category, String brand, Double minPrice, Double maxPrice) {
        log.debug("Поиск продуктов по критериям - имя: {}, категория: {}, бренд: {}, мин.цена: {}, макс.цена: {}",
                nameSubstring, category, brand, minPrice, maxPrice);
        try {
            StringBuilder sql = new StringBuilder(BASE_SEARCH);
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

            log.debug("Выполнение SQL: {}", sql);
            log.debug("С параметрами: {}", params);

            return jdbcTemplate.query(sql.toString(), productRowMapper, params.toArray());
        } catch (Exception e) {
            log.error("Ошибка поиска продуктов", e);
            throw new RepositoryException("Ошибка поиска продуктов", e);
        }
    }

    @Override
    public int count() {
        log.debug("Подсчет продуктов");
        try {
            Integer count = jdbcTemplate.queryForObject(COUNT, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Ошибка подсчета продуктов", e);
            throw new RepositoryException("Ошибка подсчета продуктов", e);
        }
    }
}
