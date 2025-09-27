package app.ecommerce.order.mapper;

import app.ecommerce.order.entity.Cart;
import app.ecommerce.order.dto.order.OrderRequestDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public final class CartMapper {
    public Cart toCart(OrderRequestDTO orderRequestDTO, BigDecimal totalAmount) {
        Cart cart = new Cart();
        cart.setReference(orderRequestDTO.reference());
        cart.setTotalAmount(totalAmount);
        cart.setPaymentMethod(orderRequestDTO.paymentMethod());
        cart.setCustomerId(orderRequestDTO.customerId());
        cart.setPaidAmount(orderRequestDTO.amount());
        return cart;
    }

}
