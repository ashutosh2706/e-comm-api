package app.ecommerce.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
    @NotNull(message = "product description is required")
    String description,
    @Positive(message = "price should be positive")
    BigDecimal price,
    @NotNull(message = "product name is required")
    String name,
    @NotNull(message = "available items is required")
    @Positive(message = "available items should be always more than 0")
    int availableItems,
    long categoryId
) {
}
