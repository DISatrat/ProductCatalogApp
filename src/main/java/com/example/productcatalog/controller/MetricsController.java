package com.example.productcatalog.controller;

import com.example.productcatalog.dto.ApiResponse;
import com.example.productcatalog.dto.metric.MetricsResponseDTO;
import com.example.productcatalog.service.metric.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST контроллер для метрик приложения.
 * <p>
 * Предоставляет конечные точки для получения метрик производительности приложения.
 * </p>
 */
@Slf4j
@Controller
@RequestMapping("/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Конечные точки метрик приложения")
public class MetricsController {

    private final MetricsService metricsService;

    /**
     * Получает все метрики приложения.
     *
     * @return данные метрик
     */
    @GetMapping
    @Operation(summary = "Получить все метрики", description = "Получает все метрики приложения")
    public ResponseEntity<ApiResponse<MetricsResponseDTO>> getAllMetrics() {
        log.debug("Получение всех метрик");

        MetricsResponseDTO metrics = MetricsResponseDTO.builder()
                .searchCount(metricsService.getSearchCount())
                .averageSearchTimeMs(metricsService.getAverageSearchTimeMs())
                .build();

        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    /**
     * Получает метрику количества поисков.
     *
     * @return количество поисков
     */
    @GetMapping("/search-count")
    @Operation(summary = "Получить количество поисков", description = "Возвращает общее количество операций поиска")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSearchCount() {
        long count = metricsService.getSearchCount();
        return ResponseEntity.ok(ApiResponse.success(Map.of("searchCount", count)));
    }

    /**
     * Получает метрику среднего времени поиска.
     *
     * @return среднее время поиска
     */
    @GetMapping("/average-time")
    @Operation(summary = "Получить среднее время поиска", description = "Возвращает среднее время операции поиска")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getAverageSearchTime() {
        double avgTime = metricsService.getAverageSearchTimeMs();
        return ResponseEntity.ok(ApiResponse.success(Map.of("averageSearchTimeMs", avgTime)));
    }
}
