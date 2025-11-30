package com.example.productcatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для данных ответа метрик.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о метриках приложения")
public class MetricsResponseDTO {

    @Schema(description = "Общее количество выполненных операций поиска", example = "1542")
    private Long searchCount;

    @Schema(description = "Среднее время операции поиска в миллисекундах", example = "45.5")
    private Double averageSearchTimeMs;
}
