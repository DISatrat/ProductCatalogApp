package com.example.productcatalog.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая продукт в каталоге.
 * <p>
 * Содержит информацию о продукте, включая название, категорию, бренд, цену и описание.
 * Отслеживает временные метки создания и изменения.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор продукта.
     */
    private Long id;

    /**
     * Название продукта.
     */
    private String name;

    /**
     * Категория продукта.
     */
    private String category;

    /**
     * Бренд продукта.
     */
    private String brand;

    /**
     * Цена продукта в денежных единицах.
     */
    private Double price;

    /**
     * Подробное описание продукта.
     */
    private String description;

    /**
     * Идентификатор пользователя, создавшего продукт.
     */
    private Long userId;

    /**
     * Временная метка создания продукта.
     */
    private LocalDateTime createdAt;

    /**
     * Временная метка последнего обновления продукта.
     */
    private LocalDateTime updatedAt;
}
