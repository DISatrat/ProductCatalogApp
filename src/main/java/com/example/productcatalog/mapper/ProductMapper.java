package com.example.productcatalog.mapper;

import com.example.productcatalog.dto.product.ProductResponseDTO;
import com.example.productcatalog.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Маппер MapStruct для сущности Product.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Преобразует сущность Product в ProductResponseDTO.
     *
     * @param product сущность продукта
     * @return DTO ответа продукта
     */
    ProductResponseDTO toDTO(Product product);

    /**
     * Преобразует список сущностей Product в список ProductResponseDTO.
     *
     * @param products список сущностей продуктов
     * @return список DTO ответов продуктов
     */
    List<ProductResponseDTO> toDTOList(List<Product> products);
}
