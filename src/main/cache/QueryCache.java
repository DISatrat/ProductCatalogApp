package main.cache;

import main.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LRU кэш для хранения результатов поиска товаров.
 * Автоматически удаляет наименее используемые записи при превышении ёмкости.
 * Потокобезопасность не гарантируется.
 */
public class QueryCache {
    /** Максимальное количество записей в кэше */
    private final int capacity;

    /** Внутреннее хранилище кэша с доступом по принципу LRU */
    private final LinkedHashMap<String, List<Product>> cache;

    /**
     * Создает новый экземпляр кэша с указанной емкостью.
     *
     * @param capacity максимальное количество записей в кэше
     * @throws IllegalArgumentException если capacity меньше или равно 0
     */
    public QueryCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<Product>> eldest) {
                return size() > QueryCache.this.capacity;
            }
        };
    }

    /**
     * Возвращает список товаров по ключу кэша.
     * Обновляет позицию записи в LRU-списке.
     *
     * @param key ключ поискового запроса
     * @return список товаров или null, если ключ не найден
     */
    public List<Product> get(String key) {
        List<Product> cachedValue = cache.get(key);
        if (cachedValue == null) {
            return null;
        }
        return new ArrayList<>(cachedValue);
    }

    /**
     * Добавляет или обновляет запись в кэше.
     * Сохраняет копию переданного списка для защиты от изменений.
     *
     * @param key ключ поискового запроса
     * @param value список товаров для кэширования
     * @throws NullPointerException если key или value равны null
     */
    public void put(String key, List<Product> value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and value cannot be null");
        }
        cache.put(key, new ArrayList<>(value));
    }

    /**
     * Полностью очищает кэш.
     * Используется при изменении данных товаров для обеспечения актуальности.
     */
    public void invalidateAll() {
        cache.clear();
    }

    /**
     * Возвращает текущее количество записей в кэше.
     *
     * @return количество закэшированных запросов
     */
    public int size() {
        return cache.size();
    }
}