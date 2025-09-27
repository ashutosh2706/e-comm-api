package app.ecommerce.order.dto.product;

import java.math.BigDecimal;

public record ProductQueryResponse(
        long productId,
        int availableQuantity,
        BigDecimal unitPrice
) {
}
