package app.ecommerce.order.client;

import app.ecommerce.order.dto.customer.CustomerResponseDTO;
import app.ecommerce.order.exception.CustomerServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
@Service
public class CustomerServiceClient {

    @Value("${app.config.customer-service}")
    private String customerService;
    @Autowired
    private RestTemplate restTemplate;

    public CustomerResponseDTO findCustomerById(long customerId, String jwt) throws CustomerServiceException {
        String requestUrl = customerService + "/" + customerId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<CustomerResponseDTO> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                requestEntity,
                CustomerResponseDTO.class
        );
        if (response.getStatusCode().is5xxServerError())
            throw new CustomerServiceException("Customer Service Down. Status: "+response.getStatusCode().value());
        if (response.getStatusCode().is2xxSuccessful())
            return response.getBody();
        if (response.getStatusCode().is4xxClientError())
            throw new CustomerServiceException("Customer Service request error. Status: "+response.getStatusCode().value());

        return null;
    }
}
