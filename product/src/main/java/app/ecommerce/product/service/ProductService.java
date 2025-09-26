package app.ecommerce.product.service;

import app.ecommerce.product.dto.*;
import app.ecommerce.product.exception.ProductPurchaseException;
import app.ecommerce.product.mapper.ProductMapper;
import app.ecommerce.product.model.Category;
import app.ecommerce.product.model.Product;
import app.ecommerce.product.repo.CategoryRepo;
import app.ecommerce.product.repo.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductMapper productMapper;

    public Object createProduct(ProductRequest request) {
        Category category = categoryRepo.findByCategoryId(request.categoryId()).orElseThrow(() -> new RuntimeException(String.format("Category id %d not found", request.categoryId())));
        Product product = new Product();
        product.setCategory(category);
        product.setDescription(request.description());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setAvailable(request.availableItems());
        Product p = productRepo.save(product);
        return productMapper.productToProductResponse(p);
    }

    public ProductPurchaseResponse purchase(long productId, int quantity) {
        var product = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException(String.format("No product found with id %d", productId)));
        if(product.getAvailable() > 0 && product.getAvailable() <= quantity) {
            throw new ProductPurchaseException(String.format("Insufficient stock available for requested product, current stock: %d", product.getAvailable()));
        }
        var currentStock = product.getAvailable();
        product.setAvailable(currentStock - quantity);
        productRepo.save(product);
        return productMapper.productToProductPurchaseResponse(product, quantity);
    }

    public List<PurchaseResponse> purchaseBulk(List<PurchaseRequest> purchaseRequests) {
        List<PurchaseResponse> responseList = new ArrayList<>();
        purchaseRequests.forEach((purchaseRequest -> {
            if(purchaseRequest.quantity() > 0) {
                var purchaseResponse = purchase(purchaseRequest.productId(), purchaseRequest.quantity());
                responseList.add(productMapper.toPurchaseResponse(purchaseResponse));
            } else {
                responseList.add(new PurchaseResponse(purchaseRequest.productId(), null, null, null, purchaseRequest.quantity()));
            }
        }));
        return responseList;
    }

    public NewProductResponse findProduct(long id) {
        return productRepo.findById(id)
                .map(productMapper::productToProductResponse)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No product was found with id %d", id)));
    }

    public List<NewProductResponse> getAll() {
        return productRepo.findAll().stream().map(productMapper::productToProductResponse).collect(Collectors.toList());
    }

    public List<ProductQueryResponse> queryProductsAvailability(List<Long> productIds) {
        List<Product> products = new ArrayList<>();
        productIds.forEach(id -> products.add(productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("No product found with id: "+id))));
        return products.stream().map(product -> new ProductQueryResponse(product.getProductId(), product.getAvailable())).toList();
    }
}
