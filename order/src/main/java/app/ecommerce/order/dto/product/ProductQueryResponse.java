package app.ecommerce.order.dto.product;

public record ProductQueryResponse(
        long productId,
        int availableQuantity
) {
}
