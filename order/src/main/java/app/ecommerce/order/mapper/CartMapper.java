package app.ecommerce.order.mapper;

import app.ecommerce.order.entity.Cart;
import app.ecommerce.order.dto.order.OrderRequestDTO;
import org.springframework.stereotype.Component;

@Component
public final class CartMapper {
    public Cart toCart(OrderRequestDTO orderRequestDTO) {
        Cart cart = new Cart();
        cart.setReference(orderRequestDTO.reference());
        cart.setTotalAmount(orderRequestDTO.amount());
        cart.setPaymentMethod(orderRequestDTO.paymentMethod());
        cart.setCustomerId(orderRequestDTO.customerId());
        return cart;
    }

}
