package app.ecommerce.notification.dto;

public record Product(
        long productId, String productName, int quantity
) {
}
