package com.example.productcatalog.controller;

import com.example.productcatalog.dto.product.ProductRequestDTO;
import com.example.productcatalog.dto.product.ProductResponseDTO;
import com.example.productcatalog.dto.product.ProductUpdateDTO;
import com.example.productcatalog.mapper.ProductMapper;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC тесты для ProductController.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("Должен вернуть все продукты")
    void getAllProducts_ShouldReturnProducts() throws Exception {
        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("Продукт 1").build(),
                Product.builder().id(2L).name("Продукт 2").build()
        );

        List<ProductResponseDTO> dtos = Arrays.asList(
                ProductResponseDTO.builder().id(1L).name("Продукт 1").build(),
                ProductResponseDTO.builder().id(2L).name("Продукт 2").build()
        );

        when(productService.getAllProducts()).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(dtos);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("Должен вернуть продукт по идентификатору")
    void getProductById_ShouldReturnProduct() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .brand("TestBrand")
                .price(99.99)
                .build();

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .brand("TestBrand")
                .price(99.99)
                .build();

        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    @DisplayName("Должен вернуть ошибку 404, когда продукт не найден")
    void getProductById_ShouldReturn404_WhenNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Должен создать новый продукт")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Новый продукт")
                .category("Electronics")
                .brand("Brand")
                .price(199.99)
                .description("Description")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Новый продукт")
                .category("Электроника")
                .brand("Бренд")
                .price(199.99)
                .description("Описание")
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(1L)
                .name("Новый продукт")
                .category("Electronics")
                .brand("Brand")
                .price(199.99)
                .build();

        when(productService.createProduct(any(ProductRequestDTO.class), eq(1L))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "1")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Новый продукт"));
    }

    @Test
    @DisplayName("Должен вернуть ошибку запроса, когда название продукта отсутствует")
    void createProduct_ShouldReturnBadRequest_WhenNameMissing() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .category("Электроника")
                .brand("Бренд")
                .price(199.99)
                .build();

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "1")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен обновить существующий продукт")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        ProductUpdateDTO request = ProductUpdateDTO.builder()
                .name("Обновленный продукт")
                .price(299.99)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Обновленный продукт")
                .price(299.99)
                .build();

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(1L)
                .name("Обновленный продукт")
                .price(299.99)
                .build();

        when(productService.updateProduct(eq(1L), any(ProductUpdateDTO.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Обновленный продукт"));
    }

    @Test
    @DisplayName("Должен удалить продукт")
    void deleteProduct_ShouldReturnSuccess() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Продукт успешно удален"));
    }

    @Test
    @DisplayName("Должен вернуть ошибку 404 при удалении несуществующего продукта")
    void deleteProduct_ShouldReturn404_WhenNotFound() throws Exception {
        when(productService.deleteProduct(999L)).thenReturn(false);

        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Должен вернуть количество продуктов")
    void getProductCount_ShouldReturnCount() throws Exception {
        when(productService.getTotalCount()).thenReturn(42);

        mockMvc.perform(get("/products/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(42));
    }
}