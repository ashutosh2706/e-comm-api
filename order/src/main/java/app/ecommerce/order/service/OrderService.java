package app.ecommerce.order.service;

import app.ecommerce.order.dto.customer.CustomerResponseDTO;
import app.ecommerce.order.dto.order.OrderResponseDTO;
import app.ecommerce.order.dto.product.ProductPurchaseResponseDTO;
import app.ecommerce.order.dto.product.ProductQueryResponse;
import app.ecommerce.order.entity.Cart;
import app.ecommerce.order.exception.CustomerServiceException;
import app.ecommerce.order.exception.OrderException;
import app.ecommerce.order.dto.order.OrderRequestDTO;
import app.ecommerce.order.client.CustomerServiceClient;
import app.ecommerce.order.client.ProductServiceClient;
import app.ecommerce.order.exception.ProductServiceException;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public Object createNewOrder(OrderRequestDTO orderRequestDTO) throws CustomerServiceException, ProductServiceException, OrderException {
        // customer service throws 5xx error on invalid customerId - handle this
        CustomerResponseDTO customerResponseDTO = customerServiceClient.findCustomerById(orderRequestDTO.customerId());
        if (customerResponseDTO == null) {
            throw new OrderException("No Customer Found with id: " + orderRequestDTO.customerId());
        }
        // Query product stocks
        List<Long> productIds = new ArrayList<>();
        orderRequestDTO.products().forEach(product -> productIds.add(product.productId()));
        List<ProductQueryResponse> availableStock =  productServiceClient.queryProductAvailability(productIds);
        if(availableStock == null) {
            throw new OrderException("Invalid productId found.");
        }

        var productStockMap = new HashMap<Long, Integer>();
        var productPriceMap = new HashMap<Long, BigDecimal>();
        availableStock.forEach(stock -> {
            productStockMap.put(stock.productId(), stock.availableQuantity());
            productPriceMap.put(stock.productId(), stock.unitPrice());
        });

        for (var product: orderRequestDTO.products()) {
            if(productStockMap.getOrDefault(product.productId(), 0) <= 0)
                throw new OrderException("Insufficient stock available for product id: "+product.productId());
        }

        // Confirm the order
        // Finally update the stocks
        //handle null
        BigDecimal paidAmount = orderRequestDTO.amount();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for(var purchase: orderRequestDTO.products()) {
            BigDecimal productUnitCost = productPriceMap.getOrDefault(purchase.productId(), BigDecimal.ZERO);
            int unitsPurchased = purchase.quantity();
            BigDecimal productTotalCost = productUnitCost.multiply(new BigDecimal(unitsPurchased));
            totalAmount = totalAmount.add(productTotalCost);
        }

        if (paidAmount.compareTo(totalAmount) < 0) {
            throw new OrderException("Insufficient payment received. Total Amount: "+totalAmount.toString()+" Paid Amount: "+paidAmount.toString());
        }

        // purchase products if everything satisfies
        List<ProductPurchaseResponseDTO> purchaseResponse = this.productServiceClient.purchaseProducts(orderRequestDTO.products());

        Cart cart = cartRepository.save(cartMapper.toCart(orderRequestDTO, totalAmount));
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
                orderRequestDTO.customerId(),
                customerResponseDTO.firstName() + " " + customerResponseDTO.lastName(),
                products
        );
        // configure DLT in kafka
        CompletableFuture<SendResult<String, Object>> future =kafkaTemplate.send(kafkaTopic, String.valueOf(confirmationMessage.customerId()), confirmationMessage);
        future.whenComplete((result, exception) -> {
            if(exception == null) {
                System.out.printf("Sent message=[%s] with offset=[%s]\n", confirmationMessage, result.getRecordMetadata().offset());
            } else {
                System.err.println("Kafka Exception: " + exception.getMessage());
            }
        });

        return new OrderResponseDTO(
                cart.getCartId(),
                customerResponseDTO.id(),
                customerResponseDTO.firstName() + " " + customerResponseDTO.lastName(),
                totalAmount,    // actual cost
                orderRequestDTO.amount(),   // paid amount
                cart.getReference(),
                cart.getPaymentMethod(),
                cart.getCreatedAt(),
                purchaseResponse    // list of products purchased
        );
    }
}
