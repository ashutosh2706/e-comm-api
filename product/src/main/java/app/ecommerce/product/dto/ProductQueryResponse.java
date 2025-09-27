package app.ecommerce.product.dto;

import java.math.BigDecimal;

public record ProductQueryResponse(
        long productId,
        int availableQuantity,
        BigDecimal unitPrice
) {
}
