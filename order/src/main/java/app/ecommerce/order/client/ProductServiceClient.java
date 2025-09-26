package app.ecommerce.order.client;

import app.ecommerce.order.exception.OrderException;
import app.ecommerce.order.dto.product.ProductPurchaseRequestDTO;
import app.ecommerce.order.dto.product.ProductPurchaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductServiceClient {

    @Value("${app.config.product-service}")
    private String productServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<ProductPurchaseResponseDTO> purchaseProducts(List<ProductPurchaseRequestDTO> purchaseRequestList) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set("x-purchase-key", "123");
        HttpEntity<List<ProductPurchaseRequestDTO>> requestEntity =  new HttpEntity<>(purchaseRequestList, headers);
        ParameterizedTypeReference<List<ProductPurchaseResponseDTO>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<ProductPurchaseResponseDTO>> responseEntity = restTemplate.exchange(productServiceUrl + "/purchase-bulk", HttpMethod.POST, requestEntity, responseType);
        if(responseEntity.getStatusCode().isError()) {
            throw new OrderException("An error occurred while purchasing products. HttpStatusCode from ProductService: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }
}
