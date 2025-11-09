package Repository;

import model.Product;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProductRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<Long, Product> storage = new HashMap<>();
    private Map<String, Set<Long>> categoryIndex = new HashMap<>();
    private Map<String, Set<Long>> brandIndex = new HashMap<>();
    private TreeMap<Double, Set<Long>> priceIndex = new TreeMap<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    public void restoreIdGenerator() {
        long max = 0;
        for (Long id : storage.keySet()) max = Math.max(max, id);
        idGenerator.set(max + 1);
    }

    public synchronized Product save(Product p) {
        if (p.getId() == 0) {
            long id = idGenerator.getAndIncrement();
            throw new IllegalStateException("Use create(ProductData) to add new product");
        } else {
            storage.put(p.getId(), p);
            indexProduct(p);
            return p;
        }
    }

    public synchronized Product create(String name, String category, String brand, double price, String description) {
        long id = idGenerator.getAndIncrement();
        Product p = new Product(id, name, category, brand, price, description);
        storage.put(id, p);
        indexProduct(p);
        return p;
    }

    private void indexProduct(Product p) {
        categoryIndex.computeIfAbsent(p.getCategory().toLowerCase(), k -> new HashSet<>()).add(p.getId());
        brandIndex.computeIfAbsent(p.getBrand().toLowerCase(), k -> new HashSet<>()).add(p.getId());
        priceIndex.computeIfAbsent(p.getPrice(), k -> new HashSet<>()).add(p.getId());
    }

    private void deindexProduct(Product p) {
        Set<Long> s;
        s = categoryIndex.get(p.getCategory().toLowerCase());
        if (s != null) { s.remove(p.getId()); if (s.isEmpty()) categoryIndex.remove(p.getCategory().toLowerCase()); }
        s = brandIndex.get(p.getBrand().toLowerCase());
        if (s != null) { s.remove(p.getId()); if (s.isEmpty()) brandIndex.remove(p.getBrand().toLowerCase()); }
        s = priceIndex.get(p.getPrice());
        if (s != null) { s.remove(p.getId()); if (s.isEmpty()) priceIndex.remove(p.getPrice()); }
    }

    public synchronized Optional<Product> findById(long id) {
        Product p = storage.get(id);
        return Optional.ofNullable(p);
    }

    public synchronized List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    public synchronized boolean delete(long id) {
        Product p = storage.remove(id);
        if (p == null) return false;
        deindexProduct(p);
        return true;
    }

    public synchronized boolean update(long id, String name, String category, String brand, Double price, String description) {
        Product p = storage.get(id);
        if (p == null) return false;
        deindexProduct(p);
        if (name != null) p.setName(name);
        if (category != null) p.setCategory(category);
        if (brand != null) p.setBrand(brand);
        if (price != null) p.setPrice(price);
        if (description != null) p.setDescription(description);
        indexProduct(p);
        return true;
    }

    // Search using indexes where possible
    public synchronized List<Product> search(String nameSubstr, String category, String brand, Double priceMin, Double priceMax) {
        Set<Long> candidateIds = null;
        if (category != null && !category.isEmpty()) {
            candidateIds = new HashSet<>(categoryIndex.getOrDefault(category.toLowerCase(), Collections.emptySet()));
        }
        if (brand != null && !brand.isEmpty()) {
            Set<Long> brandSet = brandIndex.getOrDefault(brand.toLowerCase(), Collections.emptySet());
            if (candidateIds == null) candidateIds = new HashSet<>(brandSet); else candidateIds.retainAll(brandSet);
        }
        if (priceMin != null || priceMax != null) {
            double from = priceMin == null ? priceIndex.firstKey() : priceMin;
            double to = priceMax == null ? priceIndex.lastKey() : priceMax;
            NavigableMap<Double, Set<Long>> sub = priceIndex.subMap(from, true, to, true);
            Set<Long> priceSet = new HashSet<>();
            for (Set<Long> s : sub.values()) priceSet.addAll(s);
            if (candidateIds == null) candidateIds = new HashSet<>(priceSet); else candidateIds.retainAll(priceSet);
        }
        List<Product> result;
        if (candidateIds == null) {
            // no index filter applied -> scan all
            result = new ArrayList<>(storage.values());
        } else {
            result = candidateIds.stream().map(storage::get).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (nameSubstr != null && !nameSubstr.isEmpty()) {
            String ns = nameSubstr.toLowerCase();
            result = result.stream().filter(p -> p.getName().toLowerCase().contains(ns)).collect(Collectors.toList());
        }
        result.sort(Comparator.comparingLong(Product::getId));
        return result;
    }

    public synchronized int count() {
        return storage.size();
    }
}
