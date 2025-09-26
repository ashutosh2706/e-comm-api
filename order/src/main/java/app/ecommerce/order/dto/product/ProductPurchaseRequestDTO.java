package app.ecommerce.order.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductPurchaseRequestDTO(
        @NotNull(message = "Product Id is required")
        long productId,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be valid")
        int quantity
) {
}
