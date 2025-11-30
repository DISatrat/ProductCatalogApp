package com.example.productcatalog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Реализация MetricsService с потокобезопасными счетчиками.
 */
@Slf4j
@Service
public class MetricsServiceImpl implements MetricsService {

    private final AtomicLong searchCount = new AtomicLong(0);
    private final DoubleAdder totalSearchTime = new DoubleAdder();

    @Override
    public void recordSearch(long durationMs) {
        searchCount.incrementAndGet();
        totalSearchTime.add(durationMs);
        log.debug("Поиск записан: {} ms (всего поисков: {})", durationMs, searchCount.get());
    }

    @Override
    public long getSearchCount() {
        return searchCount.get();
    }

    @Override
    public double getAverageSearchTimeMs() {
        long count = searchCount.get();
        if (count == 0) {
            return 0.0;
        }
        return totalSearchTime.sum() / count;
    }

    @Override
    public void reset() {
        searchCount.set(0);
        totalSearchTime.reset();
        log.info("Метрики сброшены");
    }
}
