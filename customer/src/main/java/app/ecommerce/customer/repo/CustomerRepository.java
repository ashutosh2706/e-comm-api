package app.ecommerce.customer.repo;

import app.ecommerce.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where c.email = :email")
    Optional<Customer> findByEmail(@Param(value = "email") String email);

    @Query("SELECT c FROM Customer c JOIN FETCH c.role")
    Page<Customer> findAllCustomer(Pageable pageable);
}
