package com.example.productcatalog.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для критериев поиска продуктов.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Критерии поиска продуктов")
public class ProductSearchDTO {

    @Schema(description = "Подстрока для поиска в названии продукта", example = "iPhone")
    private String nameSubstr;

    @Schema(description = "Фильтр по категории продукта", example = "Смартфоны")
    private String category;

    @Schema(description = "Фильтр по бренду продукта", example = "Apple")
    private String brand;

    @Schema(description = "Фильтр минимальной цены", example = "100.0")
    private Double priceMin;

    @Schema(description = "Фильтр максимальной цены", example = "1000.0")
    private Double priceMax;
}
