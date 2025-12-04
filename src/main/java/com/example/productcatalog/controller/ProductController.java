package com.example.productcatalog.controller;

import com.example.productcatalog.dto.*;
import com.example.productcatalog.dto.product.ProductRequestDTO;
import com.example.productcatalog.dto.product.ProductResponseDTO;
import com.example.productcatalog.dto.product.ProductSearchDTO;
import com.example.productcatalog.dto.product.ProductUpdateDTO;
import com.example.productcatalog.exception.EntityNotFoundException;
import com.example.productcatalog.mapper.ProductMapper;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST контроллер для операций с продуктами.
 * <p>
 * Предоставляет конечные точки CRUD для управления продуктами и поиска.
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Конечные точки управления продуктами")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * Получает все продукты.
     *
     * @return список всех продуктов
     */
    @GetMapping
    @Operation(summary = "Получить все продукты", description = "Получает все продукты в каталоге")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        log.debug("Получение всех продуктов");

        List<Product> products = productService.getAllProducts();
        List<ProductResponseDTO> dtos = productMapper.toDTOList(products);

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Получает продукт по идентификатору.
     *
     * @param id идентификатор продукта
     * @return продукт
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по идентификатору", description = "Получает конкретный продукт по его идентификатору")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Продукт найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(
            @Parameter(description = "Идентификатор продукта") @PathVariable Long id) {
        log.debug("Получение продукта по идентификатору: {}", id);

        Product product = productService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден с идентификатором: " + id));

        ProductResponseDTO dto = productMapper.toDTO(product);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /**
     * Создает новый продукт.
     *
     * @param request  запрос продукта
     * @param userId   идентификатор пользователя, создающего продукт
     * @return созданный продукт
     */
    @PostMapping
    @Operation(summary = "Создать продукт", description = "Создает новый продукт")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Продукт создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        log.info("Создание продукта: {}", request.getName());

        Product product = productService.createProduct(request, userId);
        ProductResponseDTO dto = productMapper.toDTO(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(dto, "Продукт успешно создан"));
    }

    /**
     * Обновляет существующий продукт.
     *
     * @param id      идентификатор продукта
     * @param request запрос на обновление
     * @return обновленный продукт
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт", description = "Обновляет существующий продукт")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Продукт обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @Parameter(description = "Идентификатор продукта") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO request) {
        log.info("Обновление продукта с идентификатором: {}", id);

        Product product = productService.updateProduct(id, request);
        ProductResponseDTO dto = productMapper.toDTO(product);

        return ResponseEntity.ok(ApiResponse.success(dto, "Продукт успешно обновлен"));
    }

    /**
     * Удаляет продукт.
     *
     * @param id идентификатор продукта
     * @return сообщение об успехе
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт по идентификатору")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Продукт удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "Идентификатор продукта") @PathVariable Long id) {
        log.info("Удаление продукта с идентификатором: {}", id);

        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            throw new EntityNotFoundException("Продукт не найден с идентификатором: " + id);
        }

        return ResponseEntity.ok(ApiResponse.success(null, "Продукт успешно удален"));
    }

    /**
     * Поиск продуктов по критериям.
     *
     * @param searchDTO критерии поиска
     * @return список найденных продуктов
     */
    @PostMapping("/search")
    @Operation(summary = "Поиск продуктов", description = "Поиск продуктов по различным критериям")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchProducts(
            @RequestBody ProductSearchDTO searchDTO) {
        log.debug("Поиск продуктов по критериям: {}", searchDTO);

        List<Product> products = productService.searchProducts(searchDTO);
        List<ProductResponseDTO> dtos = productMapper.toDTOList(products);

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Получает общее количество продуктов.
     *
     * @return количество продуктов
     */
    @GetMapping("/count")
    @Operation(summary = "Получить количество продуктов", description = "Возвращает общее количество продуктов")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getProductCount() {
        int count = productService.getTotalCount();
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalCount", count)));
    }
}
