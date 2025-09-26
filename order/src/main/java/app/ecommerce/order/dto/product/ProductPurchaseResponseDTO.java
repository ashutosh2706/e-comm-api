package app.ecommerce.order.dto.product;

import java.math.BigDecimal;

public record ProductPurchaseResponseDTO(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        Integer quantity
) {
}
