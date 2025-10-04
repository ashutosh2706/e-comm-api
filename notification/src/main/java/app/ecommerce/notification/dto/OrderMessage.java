package app.ecommerce.notification.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderMessage(
        long cartId,
        String reference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        long customerId,
        String customerMail,
        String customerName,
        List<Product> productList
) {
}
