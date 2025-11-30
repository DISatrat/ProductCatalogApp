package com.example.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO для создания нового продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные запроса создания продукта")
public class ProductRequestDTO {

    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 200, message = "Название продукта не должно превышать 200 символов")
    @Schema(description = "Название продукта", example = "iPhone 15 Pro", required = true)
    private String name;

    @NotBlank(message = "Категория обязательна")
    @Size(max = 100, message = "Категория не должна превышать 100 символов")
    @Schema(description = "Категория продукта", example = "Смартфоны", required = true)
    private String category;

    @NotBlank(message = "Бренд обязателен")
    @Size(max = 100, message = "Бренд не должен превышать 100 символов")
    @Schema(description = "Бренд продукта", example = "Apple", required = true)
    private String brand;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    @Schema(description = "Цена продукта", example = "999.99", required = true)
    private Double price;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    @Schema(description = "Описание продукта", example = "Последний флагманский смартфон")
    private String description;
}
