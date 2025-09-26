package app.ecommerce.order.service;

import app.ecommerce.order.dto.customer.CustomerResponseDTO;
import app.ecommerce.order.dto.product.ProductPurchaseResponseDTO;
import app.ecommerce.order.entity.Cart;
import app.ecommerce.order.exception.CustomerServiceException;
import app.ecommerce.order.exception.OrderException;
import app.ecommerce.order.dto.order.OrderRequestDTO;
import app.ecommerce.order.client.CustomerServiceClient;
import app.ecommerce.order.client.ProductServiceClient;
import app.ecommerce.order.kafka.OrderConfirmationMessage;
import app.ecommerce.order.kafka.Product;
import app.ecommerce.order.mapper.CartMapper;
import app.ecommerce.order.mapper.OrderMapper;
import app.ecommerce.order.repository.CartRepository;
import app.ecommerce.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private CustomerServiceClient customerServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${app.config.kafka.topic}")
    private String kafkaTopic;

    @Transactional
    public Object createNewOrder(OrderRequestDTO orderRequestDTO) throws CustomerServiceException {
        CustomerResponseDTO customerResponseDTO = customerServiceClient.findCustomerById(orderRequestDTO.customerId())
                .orElseThrow(() -> new OrderException(String.format("No customer present with id: %d", orderRequestDTO.customerId())));

        // Query product stocks
        // Confirm the order
        // Finally update the stocks
        List<ProductPurchaseResponseDTO> purchaseResponse = this.productServiceClient.purchaseProducts(orderRequestDTO.products());
        // Add paid column
        Cart cart = cartRepository.save(cartMapper.toCart(orderRequestDTO));
        orderRequestDTO.products().stream().map(purchaseRequestDTO -> orderMapper.toOrder(purchaseRequestDTO, cart)).forEach(order -> orderRepository.save(order));

        // process the payment,
        // send the message to kafka topic
        // read message from kafka topic
        // implement cloud gateway
        var products = purchaseResponse.stream().map(purchase -> new Product(purchase.productId(), purchase.name(), purchase.quantity())).toList();

        OrderConfirmationMessage confirmationMessage = new OrderConfirmationMessage(
                cart.getCartId(),
                cart.getReference(),
                cart.getTotalAmount(),
                cart.getPaymentMethod(),
                customerResponseDTO.firstName() + " " + customerResponseDTO.lastName(),
                products
        );
        // configure DLT in kafka
        CompletableFuture<SendResult<String, Object>> future =kafkaTemplate.send(kafkaTopic, confirmationMessage);
        future.whenComplete((result, exception) -> {
            if(exception == null) {
                System.out.printf("Sent message=[%s] with offset=[%s]\n", confirmationMessage, result.getRecordMetadata().offset());
            } else {
                System.err.println("Kafka Exception: " + exception.getMessage());
            }
        });
        Map<String, Object> orderResponse = new HashMap<>();
        orderResponse.put("cartId", cart.getCartId());
        orderResponse.put("customerId", customerResponseDTO.id());
        orderResponse.put("customerName", customerResponseDTO.firstName() + " " + customerResponseDTO.lastName());
        orderResponse.put("reference", cart.getReference());
        orderResponse.put("payment", cart.getPaymentMethod());
        orderResponse.put("purchasedProducts", purchaseResponse);
        return orderResponse;
    }
}
