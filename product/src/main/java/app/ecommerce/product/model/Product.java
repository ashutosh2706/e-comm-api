package app.ecommerce.product.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "t_product")
public class Product {

    @Id
    @GeneratedValue
    private long productId;
    private String description;
    private BigDecimal price;
    private int available;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
    private String name;

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product() {
    }

    public Product(long productId, String description, BigDecimal price, Category category, String name, int available) {
        this.productId = productId;
        this.description = description;
        this.price = price;
        this.category = category;
        this.name = name;
        this.available = available;
    }
}
