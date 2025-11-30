package com.example.productcatalog.controller;

import com.example.productcatalog.service.metric.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC тесты для MetricsController.
 */
@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private MetricsController metricsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
    }

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
    @DisplayName("Должен вернуть количество поисков")
    void getSearchCount_ShouldReturnCount() throws Exception {
        when(metricsService.getSearchCount()).thenReturn(42L);

        mockMvc.perform(get("/metrics/search-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.searchCount").value(42));
    }

    @Test
    @DisplayName("Должен вернуть среднее время поиска")
    void getAverageSearchTime_ShouldReturnTime() throws Exception {
        when(metricsService.getAverageSearchTimeMs()).thenReturn(123.45);

        mockMvc.perform(get("/metrics/average-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageSearchTimeMs").value(123.45));
    }

    @Test
    @DisplayName("Должен вернуть нулевые метрики, если поиски не выполнялись")
    void getAllMetrics_ShouldReturnZero_WhenNoSearches() throws Exception {
        when(metricsService.getSearchCount()).thenReturn(0L);
        when(metricsService.getAverageSearchTimeMs()).thenReturn(0.0);

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.searchCount").value(0))
                .andExpect(jsonPath("$.data.averageSearchTimeMs").value(0.0));
    }
}