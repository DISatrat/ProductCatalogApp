package service;

import Repository.ProductRepository;
import Repository.UserRepository;
import cache.QueryCache;
import model.Product;

import java.util.List;
import java.util.Optional;

public class CatalogService {
    private final ProductRepository repo;
    private final UserRepository userRepo;
    private final QueryCache cache;
    private final AuditService audit;

    // metrics
    private long searchCount = 0;
    private long totalSearchTimeNs = 0;

    public CatalogService(ProductRepository repo, UserRepository userRepo, QueryCache cache, AuditService audit) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.cache = cache;
        this.audit = audit;
    }

    public Product addProduct(String username, String name, String category, String brand, double price, String description) {
        Product p = repo.create(name, category, brand, price, description);
        cache.invalidateAll();
        audit.record(username, "ADD_PRODUCT", "id=" + p.getId() + " name=" + name);
        return p;
    }

    public boolean updateProduct(String username, long id, String name, String category, String brand, Double price, String description) {
        boolean ok = repo.update(id, name, category, brand, price, description);
        if (ok) {
            cache.invalidateAll();
            audit.record(username, "UPDATE_PRODUCT", "id=" + id);
        }
        return ok;
    }

    public boolean deleteProduct(String username, long id) {
        boolean ok = repo.delete(id);
        if (ok) {
            cache.invalidateAll();
            audit.record(username, "DELETE_PRODUCT", "id=" + id);
        }
        return ok;
    }

    public Optional<Product> getById(long id) {
        return repo.findById(id);
    }

    public List<Product> search(String username, String nameSubstr, String category, String brand, Double priceMin, Double priceMax) {
        String key = String.format("n=%s|c=%s|b=%s|min=%s|max=%s",
                nameSubstr, category, brand, priceMin, priceMax);
        List<Product> cached = cache.get(key);
        long start = System.nanoTime();
        if (cached != null) {
            long duration = System.nanoTime() - start;
            synchronized (this) {
                searchCount++;
                totalSearchTimeNs += duration;
            }
            audit.record(username, "SEARCH_CACHE_HIT", key);
            return cached;
        }
        List<Product> result = repo.search(nameSubstr, category, brand, priceMin, priceMax);
        cache.put(key, result);
        long duration = System.nanoTime() - start;
        synchronized (this) {
            searchCount++;
            totalSearchTimeNs += duration;
        }
        audit.record(username, "SEARCH", key + " -> " + result.size() + " results");
        return result;
    }

    public int totalProducts() {
        return repo.count();
    }

    public synchronized long getSearchCount() { return searchCount; }
    public synchronized double getAverageSearchMs() {
        if (searchCount == 0) return 0;
        return (totalSearchTimeNs / 1_000_000.0) / searchCount;
    }

    public void invalidateCache() {
        cache.invalidateAll();
    }
}
