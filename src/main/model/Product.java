package main.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Представляет товар в системе каталога.
 * Содержит основную информацию о товаре и временные метки создания/обновления.
 * Класс поддерживает сериализацию для данного хранения.
 */
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Уникальный идентификатор товара */
    private final long id;

    /** Название товара */
    private String name;

    /** Категория товара */
    private String category;

    /** Бренд товара */
    private String brand;

    /** Цена товара */
    private double price;

    /** Описание товара */
    private String description;

    /** Дата и время создания записи о товаре */
    private Date createdAt;

    /** Дата и время последнего обновления товара */
    private Date updatedAt;

    /**
     * Создает новый товар с указанными параметрами.
     * Устанавливает временные метки создания и обновления на текущее время.
     *
     * @param id уникальный идентификатор товара
     * @param name название товара
     * @param category категория товара
     * @param brand бренд товара
     * @param price цена товара
     * @param description описание товара
     * @throws IllegalArgumentException если price отрицательный
     * @throws NullPointerException если name, category, brand или description равны null
     */
    public Product(long id, String name, String category, String brand, double price, String description) {
        if (name == null || category == null || brand == null || description == null) {
            throw new NullPointerException("Product fields cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; touch(); }
    public void setCategory(String category) { this.category = category; touch(); }
    public void setBrand(String brand) { this.brand = brand; touch(); }
    public void setPrice(double price) { this.price = price; touch(); }
    public void setDescription(String description) { this.description = description; touch(); }


    /**
     * Обновляет временную метку modifiedAt на текущее время.
     * Используется при изменении данных товара.
     */
    private void touch() {
        this.updatedAt = new Date();
    }


    @Override
    public String toString() {
        return String.format("ID:%d | %s | %s | %s | %.2f\n  %s", id, name, category, brand, price, description);
    }
}


