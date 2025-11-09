package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Product(long id, String name, String category, String brand, double price, String description) {
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

    private void touch() {
        this.updatedAt = new Date();
    }

    @Override
    public String toString() {
        return String.format("ID:%d | %s | %s | %s | %.2f\n  %s", id, name, category, brand, price, description);
    }
}


