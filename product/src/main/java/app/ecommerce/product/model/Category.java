package app.ecommerce.product.model;

import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "t_category")
public class Category {

    @Id
    @GeneratedValue
    private long categoryId;
    private String description;
    private String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Product> products;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Category() {
    }

    public Category(long categoryId, String description, String name, List<Product> products) {
        this.categoryId = categoryId;
        this.description = description;
        this.name = name;
        this.products = products;
    }
}
