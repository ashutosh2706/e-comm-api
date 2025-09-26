package app.ecommerce.product.repo;

import app.ecommerce.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    @Query("Select c from Category c where c.categoryId = :id")
    Optional<Category> findByCategoryId(@Param(value = "id") long id);
}
