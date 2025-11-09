package cache;

import model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryCache {
    private final int capacity;
    private final LinkedHashMap<String, List<Product>> cache;

    public QueryCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<String, List<Product>>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<Product>> eldest) {
                return size() > QueryCache.this.capacity;
            }
        };
    }

    public synchronized List<Product> get(String key) {
        List<Product> v = cache.get(key);
        if (v == null) return null;
        return new ArrayList<>(v);
    }

    public synchronized void put(String key, List<Product> value) {
        cache.put(key, new ArrayList<>(value));
    }
    public synchronized void invalidateAll() {
        cache.clear();
    }

    public synchronized int size() { return cache.size(); }
}
