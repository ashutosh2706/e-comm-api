package app.ecommerce.product.repo;

import app.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findById(long productId);
    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllProduct();
}
