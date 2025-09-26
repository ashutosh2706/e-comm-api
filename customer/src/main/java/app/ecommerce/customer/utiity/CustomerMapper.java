package app.ecommerce.customer.utiity;


import app.ecommerce.customer.entity.Customer;
import app.ecommerce.customer.dto.CustomerRequestDto;
import org.springframework.stereotype.Component;

@Component
public final class CustomerMapper {
    public Customer customerRequestDtoToCustomer(CustomerRequestDto customerRequestDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequestDto.firstName());
        customer.setLastName(customerRequestDto.lastName());
        customer.setEmail(customerRequestDto.email());
        return customer;
    }

}
