package main.controller;

import main.service.metrics.MetricsService;

/**
 * Контроллер для предоставления метрик производительности системы.
 * Агрегирует данные о производительности поисковых операций.
 */
public class MetricsController {
    /** Сервис для доступа к метрикам производительности */
    private final MetricsService metricsService;

    /**
     * Конструктор метрик
     *
     * @param metricsService сервис для доступа к данным метрик
     * @throws NullPointerException если metricsService равен null
     */
    public MetricsController(MetricsService metricsService) {
        if (metricsService == null) {
            throw new NullPointerException("MetricsService cannot be null");
        }
        this.metricsService = metricsService;
    }

    /**
     * Возвращает общее количество выполненных поисковых запросов.
     *
     * @return количество поисковых операций
     */
    public long getSearchCount() {
        return metricsService.getSearchCount();
    }

    /**
     * Возвращает среднее время выполнения поисковых запросов в миллисекундах.
     *
     * @return среднее время поиска в миллисекундах
     */
    public double getAverageSearchTimeMs() {
        return metricsService.getAverageSearchTimeMs();
    }
}