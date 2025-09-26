package app.ecommerce.product.mapper;

import app.ecommerce.product.model.Product;
import app.ecommerce.product.dto.NewProductResponse;
import app.ecommerce.product.dto.ProductPurchaseResponse;
import app.ecommerce.product.dto.PurchaseResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public final class ProductMapper {
    public NewProductResponse productToProductResponse(Product product) {
        return new NewProductResponse(product.getProductId(), product.getName(), product.getDescription(), product.getCategory().getName(), product.getPrice(), product.getAvailable());
    }

    public ProductPurchaseResponse productToProductPurchaseResponse(Product product, int qty) {
        return new ProductPurchaseResponse(product.getProductId(), product.getDescription(), product.getPrice().multiply(new BigDecimal(qty)), qty, product.getCategory().getName(), product.getName());
    }

    public PurchaseResponse toPurchaseResponse(ProductPurchaseResponse response) {
        return new PurchaseResponse(response.productId(), response.productName(), response.description(),response.totalPrice(), response.quantityPurchased());
    }

}
