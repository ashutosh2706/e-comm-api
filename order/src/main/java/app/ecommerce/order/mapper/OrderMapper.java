package app.ecommerce.order.mapper;

import app.ecommerce.order.dto.product.ProductPurchaseRequestDTO;
import app.ecommerce.order.entity.Cart;
import app.ecommerce.order.entity.Order;
import org.springframework.stereotype.Component;

@Component
public final class OrderMapper {
    public Order toOrder(ProductPurchaseRequestDTO request, Cart cart) {
        Order order = new Order();
        order.setCart(cart);
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        return order;
    }
}
