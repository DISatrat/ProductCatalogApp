package com.example.productcatalog.service.metric;

/**
 * Интерфейс сервиса для метрик приложения.
 */
public interface MetricsService {

    /**
     * Записывает операцию поиска с её временем выполнения.
     *
     * @param durationMs продолжительность поиска в миллисекундах
     */
    void recordSearch(long durationMs);

    /**
     * Получает общее количество операций поиска.
     *
     * @return количество поисков
     */
    long getSearchCount();

    /**
     * Получает среднее время поиска в миллисекундах.
     *
     * @return среднее время поиска
     */
    double getAverageSearchTimeMs();

    /**
     * Сбрасывает все метрики.
     */
    void reset();
}
