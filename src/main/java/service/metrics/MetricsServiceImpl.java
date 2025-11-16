package service.metrics;

/**
 * Реализация сервиса метрик для сбора статистики поисковых операций.
 * Хранит метрики в памяти и предоставляет методы для их получения.
 */
public class MetricsServiceImpl implements MetricsService {
    private long searchCount = 0;
    private long totalSearchTimeNs = 0;

    @Override
    public void recordSearch(long durationNanoseconds) {
        searchCount++;
        totalSearchTimeNs += durationNanoseconds;
    }

    @Override
    public long getSearchCount() {
        return searchCount;
    }

    @Override
    public double getAverageSearchTimeMs() {
        if (searchCount == 0) return 0;
        return (totalSearchTimeNs / 1_000_000.0) / searchCount;
    }
}