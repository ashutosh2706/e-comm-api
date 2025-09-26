package app.ecommerce.order.dto.order;

import app.ecommerce.order.dto.product.ProductPurchaseRequestDTO;
import app.ecommerce.order.common.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record  OrderRequestDTO(
        long orderId,
        String reference,
        @Positive(message = "Order amount should be positive")
        BigDecimal amount,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer Id is required")
        long customerId,
        @NotEmpty(message = "At least one product must be purchased with each order")
        List<ProductPurchaseRequestDTO> products) {

}
