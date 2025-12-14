package com.example.productcatalog.controller;

import com.example.productcatalog.service.metric.MetricsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для MetricsController
 */
@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricsService metricsService;

    @Test
    @DisplayName("Должен вернуть все метрики")
    void getAllMetrics_ShouldReturnMetrics() throws Exception {
        when(metricsService.getSearchCount()).thenReturn(100L);
        when(metricsService.getAverageSearchTimeMs()).thenReturn(45.5);

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.searchCount").value(100))
                .andExpect(jsonPath("$.data.averageSearchTimeMs").value(45.5));
    }

    @Test
    @DisplayName("Должен вернуть количество поисковых запросов")
    void getSearchCount_ShouldReturnCount() throws Exception {
        when(metricsService.getSearchCount()).thenReturn(50L);

        mockMvc.perform(get("/metrics/search-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.searchCount").value(50));
    }

    @Test
    @DisplayName("Должен вернуть среднее время поиска")
    void getAverageTime_ShouldReturnTime() throws Exception {
        when(metricsService.getAverageSearchTimeMs()).thenReturn(32.3);

        mockMvc.perform(get("/metrics/average-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageTimeMs").value(32.3));
    }
}
