package app.ecommerce.customer.repo;

import app.ecommerce.customer.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r where r.roleId = :role_id")
    Optional<Role> findByRoleId(@Param(value = "role_id") int roleId);
}
