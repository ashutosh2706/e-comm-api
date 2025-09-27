package app.ecommerce.order.client;

import app.ecommerce.order.dto.customer.CustomerResponseDTO;
import app.ecommerce.order.dto.product.ProductQueryResponse;
import app.ecommerce.order.exception.CustomerServiceException;
import app.ecommerce.order.exception.OrderException;
import app.ecommerce.order.dto.product.ProductPurchaseRequestDTO;
import app.ecommerce.order.dto.product.ProductPurchaseResponseDTO;
import app.ecommerce.order.exception.ProductServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceClient {

    @Value("${app.config.product-service}")
    private String productServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<ProductPurchaseResponseDTO> purchaseProducts(List<ProductPurchaseRequestDTO> purchaseRequestList) throws ProductServiceException {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set("x-purchase-key", "123");
        HttpEntity<List<ProductPurchaseRequestDTO>> requestEntity =  new HttpEntity<>(purchaseRequestList, headers);
        ParameterizedTypeReference<List<ProductPurchaseResponseDTO>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<ProductPurchaseResponseDTO>> responseEntity = restTemplate.exchange(productServiceUrl + "/purchase-bulk", HttpMethod.POST, requestEntity, responseType);
        if(responseEntity.getStatusCode().is5xxServerError()) {
            throw new ProductServiceException("Product Service is down");
        } if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    public List<ProductQueryResponse> queryProductAvailability(List<Long> productIds) throws ProductServiceException {
        String requestUrl = productServiceUrl + "/query-products";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Long>> payload = new HttpEntity<>(productIds, headers);
        ResponseEntity<List<ProductQueryResponse>> responseEntity = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                payload,
                new ParameterizedTypeReference<List<ProductQueryResponse>>() {}
        );

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else if(responseEntity.getStatusCode().is5xxServerError()) {
            throw new ProductServiceException("Product Service is down");
        }
        return null;
    }
}
