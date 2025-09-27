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

    public CustomerResponseDTO findCustomerById(long customerId) throws CustomerServiceException {
        String requestUrl = customerService + "/" + customerId;
        ResponseEntity<CustomerResponseDTO> response = restTemplate.getForEntity(
                requestUrl, CustomerResponseDTO.class
        );
        if (response.getStatusCode().is5xxServerError())
            throw new CustomerServiceException("Customer Service Down");
        if (response.getStatusCode().is2xxSuccessful())
            return response.getBody();
        // return null for 4xx error
        return null;
    }
}
