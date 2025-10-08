package app.ecommerce.customer.controller;


import app.ecommerce.customer.dto.CustomerRequestDto;
import app.ecommerce.customer.exception.KeyCloakServiceException;
import app.ecommerce.customer.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(value = "add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCustomer(@Valid @RequestBody CustomerRequestDto request) {
        try {
            return ResponseEntity.created(URI.create("/customer")).body(customerService.createCustomer(request));
        } catch (KeyCloakServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(value = "update", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCustomer(
            @Valid @RequestBody CustomerRequestDto requestDto,
            @RequestParam(value = "customer_id") long customerId) {
        try{
            return ResponseEntity.ok().body(
                    customerService.updateCustomer(customerId, requestDto)
            );
        } catch (KeyCloakServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(value = "page_number", required = false, defaultValue = "1") int page,
            @RequestParam(value = "page_size", required = false, defaultValue = "5") int pageSize) {
        return ResponseEntity.ok().body(
                customerService.getAllCustomer(page, pageSize)
        );
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable(name = "id") long customerId) {
        try {
            return ResponseEntity.ok().body(customerService.findByCustomerId(customerId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") long customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (KeyCloakServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
