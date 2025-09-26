package app.ecommerce.order.client;

import app.ecommerce.order.dto.customer.CustomerResponseDTO;
import app.ecommerce.order.exception.CustomerServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
@Service
public class CustomerServiceClient {

    @Value("${app.config.customer-service}")
    private String customerService;
    @Autowired
    private RestTemplate restTemplate;

    public Optional<CustomerResponseDTO> findCustomerById(long customerId) throws CustomerServiceException {
        String requestUrl = customerService + "/" + customerId;
        ResponseEntity<CustomerResponseDTO> response = restTemplate.getForEntity(
                requestUrl, CustomerResponseDTO.class
        );
        if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
            throw new CustomerServiceException("Customer Service Down");
        return response.getStatusCode() == HttpStatus.OK && response.getBody() != null ?
                Optional.of(response.getBody()) :
                Optional.empty();
    }
}
