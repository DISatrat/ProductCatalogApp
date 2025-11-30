package com.example.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO для данных ответа продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с информацией о продукте")
public class ProductResponseDTO {

    @Schema(description = "Идентификатор продукта", example = "1")
    private Long id;

    @Schema(description = "Название продукта", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "Категория продукта", example = "Смартфоны")
    private String category;

    @Schema(description = "Бренд продукта", example = "Apple")
    private String brand;

    @Schema(description = "Цена продукта", example = "999.99")
    private Double price;

    @Schema(description = "Описание продукта", example = "Последний флагманский смартфон")
    private String description;

    @Schema(description = "Идентификатор пользователя, создавшего продукт", example = "1")
    private Long userId;

    @Schema(description = "Временная метка создания")
    private LocalDateTime createdAt;

    @Schema(description = "Временная метка последнего обновления")
    private LocalDateTime updatedAt;
}
