package app.ecommerce.product.dto;

public record ProductQueryResponse(
        long productId,
        int availableQuantity
) {
}
