package app.ecommerce.order.controller;

import app.ecommerce.order.dto.order.OrderRequestDTO;
import app.ecommerce.order.exception.CustomerServiceException;
import app.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO, @RequestHeader Map<String, String> headers) throws CustomerServiceException {
        return ResponseEntity.status(HttpStatus.CREATED).header("path", "/order").body(orderService.createNewOrder(orderRequestDTO));
    }

}
