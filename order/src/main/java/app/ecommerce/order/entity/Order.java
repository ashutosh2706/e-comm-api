package app.ecommerce.order.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "t_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "t_order_seq", allocationSize = 1)
    private long orderId;
    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;
    @Column(nullable = false)
    private long productId;
    private Integer quantity;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Order(int orderId, Cart cart, Long productId, Integer quantity) {
        this.orderId = orderId;
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Order() {}
}
