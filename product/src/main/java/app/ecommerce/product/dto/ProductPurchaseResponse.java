package app.ecommerce.product.dto;

import app.ecommerce.product.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProductPurchaseResponse(
        @JsonProperty(value = "product_id") long productId,
        @JsonProperty(value = "description") String description,
        @JsonProperty(value = "total_price") BigDecimal totalPrice,
        @JsonProperty(value = "quantity_purchased") int quantityPurchased,
        @JsonProperty(value = "product_category") String productCategory,
        @JsonProperty(value = "product_name") String productName
) {
}
