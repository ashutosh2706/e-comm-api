package app.ecommerce.order.kafka;

import app.ecommerce.order.common.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderConfirmationMessage (
        long cartId,
        String reference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        String customerName,
        List<Product> productList
) {
}
