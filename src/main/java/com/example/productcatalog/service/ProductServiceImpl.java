package com.example.productcatalog.service;

import com.example.productcatalog.dto.ProductRequestDTO;
import com.example.productcatalog.dto.ProductSearchDTO;
import com.example.productcatalog.dto.ProductUpdateDTO;
import com.example.productcatalog.exception.EntityNotFoundException;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация ProductService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(ProductRequestDTO request, Long userId) {
        log.info("Создание продукта: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .brand(request.getBrand())
                .price(request.getPrice())
                .description(request.getDescription())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("Продукт создан с идентификатором: {}", saved.getId());
        return saved;
    }

    @Override
    public Product updateProduct(Long id, ProductUpdateDTO request) {
        log.info("Обновление продукта с идентификатором: {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден с идентификатором: " + id));

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getCategory() != null) {
            existing.setCategory(request.getCategory());
        }
        if (request.getBrand() != null) {
            existing.setBrand(request.getBrand());
        }
        if (request.getPrice() != null) {
            existing.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }

        Product updated = productRepository.update(existing);
        log.info("Продукт обновлен с идентификатором: {}", id);
        return updated;
    }

    @Override
    public Optional<Product> findById(Long id) {
        log.debug("Поиск продукта по идентификатору: {}", id);
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        log.debug("Получение всех продуктов");
        return productRepository.findAll();
    }

    @Override
    public boolean deleteProduct(Long id) {
        log.info("Удаление продукта с идентификатором: {}", id);
        boolean deleted = productRepository.deleteById(id);
        if (deleted) {
            log.info("Продукт удален с идентификатором: {}", id);
        }
        return deleted;
    }

    @Override
    public List<Product> searchProducts(ProductSearchDTO searchDTO) {
        log.debug("Поиск продуктов по критериям: {}", searchDTO);
        return productRepository.search(
                searchDTO.getNameSubstr(),
                searchDTO.getCategory(),
                searchDTO.getBrand(),
                searchDTO.getPriceMin(),
                searchDTO.getPriceMax()
        );
    }

    @Override
    public int getTotalCount() {
        return productRepository.count();
    }
}
