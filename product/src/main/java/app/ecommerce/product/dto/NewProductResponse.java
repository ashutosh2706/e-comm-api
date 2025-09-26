package app.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record NewProductResponse(
        long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        @JsonProperty(value = "available_quantity") int availableItems
) {
}
