package main.service.metrics;

/**
 * Сервис для сбора и предоставления метрик производительности системы.
 * Отслеживает статистику поисковых операций.
 */
public interface MetricsService {
    /**
     * Регистрирует выполнение поисковой операции для сбора статистики.
     *
     * @param durationNanoseconds время выполнения поиска в наносекундах
     */
    void recordSearch(long durationNanoseconds);

    /**
     * Возвращает общее количество выполненных поисковых операций.
     *
     * @return количество поисковых запросов
     */
    long getSearchCount();

    /**
     * Возвращает среднее время выполнения поисковых операций.
     *
     * @return среднее время поиска в миллисекундах
     */
    double getAverageSearchTimeMs();
}