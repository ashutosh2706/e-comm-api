package app.ecommerce.customer.service;

import app.ecommerce.customer.entity.Customer;
import app.ecommerce.customer.dto.CustomerRequestDto;
import app.ecommerce.customer.dto.CustomerResponseDto;
import app.ecommerce.customer.repo.CustomerRepository;
import app.ecommerce.customer.utiity.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public String createCustomer(CustomerRequestDto request) {
        Customer customer = customerMapper.customerRequestDtoToCustomer(request);
        var savedCustomer = customerRepository.save(customer);
        return String.format("Customer Id: %d", savedCustomer.getId());
    }

    @Transactional
    public Customer updateCustomer(long customerId, CustomerRequestDto requestDto) {
        Customer customer = customerMapper.customerRequestDtoToCustomer(requestDto);
        Optional<Customer> customerFromDb = customerRepository.findById(customerId);
        if(customerFromDb.isPresent()) {
            var customerDb = customerFromDb.get();
            customerDb.setFirstName(customer.getFirstName());
            customerDb.setLastName(customer.getLastName());
            customerDb.setEmail(customer.getEmail());
            return customerRepository.save(customerDb);

        } else
            throw new IllegalArgumentException(String.format("No customer present with customerId: %d", customerId));
    }

    public CustomerResponseDto<Customer> getAllCustomer(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<Customer> customersPage = customerRepository.findAll(pageRequest);
        return new CustomerResponseDto<>(
                page, customersPage.getNumberOfElements(), customersPage.getTotalElements(), customersPage.getContent()
        );
    }

    public Customer findByCustomerId(long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException(String.format("No customer found with customerId %d", customerId)));
    }

    public void deleteCustomer(long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()) {
            customerRepository.delete(optionalCustomer.get());
            return;
        }
        throw new EntityNotFoundException("No customer found with id: " + customerId);
    }
}
