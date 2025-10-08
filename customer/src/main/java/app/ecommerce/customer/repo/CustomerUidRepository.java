package app.ecommerce.customer.repo;

import app.ecommerce.customer.entity.Customer;
import app.ecommerce.customer.entity.CustomerUid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerUidRepository extends JpaRepository<CustomerUid, Long> {

    @Query("FROM CustomerUid a WHERE a.customer.id = :customerId")
    Optional<CustomerUid> findByCustomerId(@Param(value = "customerId") long customerId);
}
