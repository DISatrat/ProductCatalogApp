package com.example.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO для обновления существующего продукта.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные запроса обновления продукта")
public class ProductUpdateDTO {

    @Size(max = 200, message = "Название продукта не должно превышать 200 символов")
    @Schema(description = "Название продукта", example = "iPhone 15 Pro Max")
    private String name;

    @Size(max = 100, message = "Категория не должна превышать 100 символов")
    @Schema(description = "Категория продукта", example = "Смартфоны")
    private String category;

    @Size(max = 100, message = "Бренд не должен превышать 100 символов")
    @Schema(description = "Бренд продукта", example = "Apple")
    private String brand;

    @Positive(message = "Цена должна быть положительной")
    @Schema(description = "Цена продукта", example = "1099.99")
    private Double price;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    @Schema(description = "Описание продукта", example = "Обновленный флагманский смартфон")
    private String description;
}
