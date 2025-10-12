package app.ecommerce.customer.service;

import app.ecommerce.customer.entity.Customer;
import app.ecommerce.customer.dto.CustomerRequestDto;
import app.ecommerce.customer.dto.CustomerResponseDto;
import app.ecommerce.customer.entity.CustomerUid;
import app.ecommerce.customer.entity.Role;
import app.ecommerce.customer.exception.KeyCloakServiceException;
import app.ecommerce.customer.repo.CustomerRepository;
import app.ecommerce.customer.repo.CustomerRoleRepository;
import app.ecommerce.customer.repo.CustomerUidRepository;
import app.ecommerce.customer.utiity.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerRoleRepository roleRepository;
    private final CustomerMapper customerMapper;
    private final KeycloakService keycloakService;
    private final CustomerUidRepository uidRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, CustomerRoleRepository roleRepository, KeycloakService keycloakService, CustomerUidRepository uidRepository) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.roleRepository = roleRepository;
        this.uidRepository = uidRepository;
        this.keycloakService = keycloakService;
    }

    @CacheEvict(value = "customer", allEntries = true)
    @Transactional
    public Object createCustomer(CustomerRequestDto request) throws KeyCloakServiceException {
        Role role = roleRepository.findByRoleId(request.roleId()).orElseThrow(() -> new RuntimeException("No role found with roleId: "+request.roleId()));
        Customer customer = customerMapper.customerRequestDtoToCustomer(request);
        customer.setRole(role);
        var kcUid = keycloakService.createUser(request.firstName(), request.lastName(), request.email());
        keycloakService.assignRole(role, kcUid);
        var savedCustomer = customerRepository.save(customer);
        var customerUid = new CustomerUid();
        customerUid.setCustomer(savedCustomer);
        customerUid.setUuid(UUID.fromString(kcUid));
        return uidRepository.save(customerUid);
    }

    @CacheEvict(value = "customer", allEntries = true)
    @Transactional
    public Customer updateCustomer(long customerId, CustomerRequestDto requestDto) throws KeyCloakServiceException {
        Role role = roleRepository.findByRoleId(requestDto.roleId()).orElseThrow(() -> new EntityNotFoundException("No role found with roleId: "+requestDto.roleId()));
        Customer customer = customerMapper.customerRequestDtoToCustomer(requestDto);
        Optional<Customer> customerFromDb = customerRepository.findById(customerId);
        // get uid to update details in keycloak
        CustomerUid customerUid = uidRepository.findByCustomerId(customerId).orElseThrow(() -> new EntityNotFoundException("Customer Id: "+customerId+" not found"));
        if(customerFromDb.isPresent()) {
            // update kc user details
            keycloakService.updateUserDetails(customerUid.getUuid(), customer.getFirstName(), customer.getLastName(), role);
            var customerDb = customerFromDb.get();
            customerDb.setFirstName(customer.getFirstName());
            customerDb.setLastName(customer.getLastName());
            /* customerDb.setEmail(customer.getEmail()); */
            customerDb.setRole(role);
            return customerRepository.save(customerDb);

        } else
            throw new IllegalArgumentException(String.format("No customer present with customerId: %d", customerId));
    }

    @Cacheable(value = "customer", key = "'page_' + #page + '_size_' + #pageSize")
    public CustomerResponseDto<Customer> getAllCustomer(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.ASC, "id"));
        Page<Customer> customersPage = customerRepository.findAllCustomer(pageRequest);
        return new CustomerResponseDto<>(
                page, customersPage.getNumberOfElements(), customersPage.getTotalElements(), customersPage.getContent()
        );
    }

    @Cacheable(value = "customer", key = "#customerId")
    public Customer findByCustomerId(long customerId) {
        System.out.println("DB called for customerId: " + customerId);
        return customerRepository.findById(customerId).orElseThrow(() -> new EntityNotFoundException(String.format("No customer found with customerId %d", customerId)));
    }

    @CacheEvict(value = "customer", allEntries = true)
    @Transactional
    public void deleteCustomer(long customerId) throws KeyCloakServiceException {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if(optionalCustomer.isPresent()) {
            var customerUID = uidRepository.findByCustomerId(optionalCustomer.get().getId()).orElseThrow(() -> new EntityNotFoundException("No customer found with id: "+customerId));
            keycloakService.deleteUser(customerUID.getUuid());
            uidRepository.delete(customerUID);
            customerRepository.delete(optionalCustomer.get());
            return;
        }
        throw new EntityNotFoundException("No customer found with id: " + customerId);
    }
}
