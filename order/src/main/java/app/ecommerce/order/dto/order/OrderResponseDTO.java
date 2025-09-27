package app.ecommerce.order.dto.order;

import app.ecommerce.order.common.PaymentMethod;
import app.ecommerce.order.dto.product.ProductPurchaseResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        @JsonProperty(value = "cart_id") long cartId,
        @JsonProperty(value = "customer_id") long customerId,
        @JsonProperty(value = "customer_name") String customerName,
        @JsonProperty(value = "total_amount") BigDecimal totalAmount,
        @JsonProperty(value = "paid_amount") BigDecimal paidAmount,
        String reference,
        @JsonProperty(value = "payment_mode") PaymentMethod paymentMode,
        @JsonProperty(value = "order_at") LocalDateTime orderAt,
        @JsonProperty(value = "orders_list") List<ProductPurchaseResponseDTO> purchaseResponse

) {
}
