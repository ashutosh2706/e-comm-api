package app.ecommerce.product.controller;

import app.ecommerce.product.exception.ProductPurchaseException;
import app.ecommerce.product.dto.ProductRequest;
import app.ecommerce.product.dto.PurchaseRequest;
import app.ecommerce.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductRequest request) {
        try {
            return ResponseEntity.created(URI.create("/product")).body(productService.createProduct(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping(value = "/purchase-bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bulkPurchase(@Valid @RequestBody List<PurchaseRequest> purchaseRequestList, @RequestHeader Map<String, String> headers) {
        if(headers.containsKey("x-purchase-key")) {
            var value = headers.getOrDefault("x-purchase-key", "");
            if(!value.trim().equals("123")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid api key");
            }
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("provide api key");

        try {
            return ResponseEntity.ok(productService.purchaseBulk(purchaseRequestList));
        } catch (ProductPurchaseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/purchase", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> purchaseProduct(
            @RequestParam(value = "product_id") long productId,
            @RequestParam(value = "quantity") int quantity
    ) {
        if(quantity <= 0) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(String.format("Can't purchase %d quantity", quantity));
        }
        try {
            return ResponseEntity.ok(productService.purchase(productId, quantity));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ProductPurchaseException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findProduct(@PathVariable(value = "id") long id) {
        try {
            return ResponseEntity.ok(productService.findProduct(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(
            value = "query-products",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> queryProducts(@RequestBody List<Long> productIds) {
        var result = productService.queryProductsAvailability(productIds);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(productService.getAll());
    }
}
