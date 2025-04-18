package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Keep existing methods
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySkuIgnoreCase(String sku);
    Optional<Product> findByNameIgnoreCase(String name);
    Optional<Product> findBySkuIgnoreCase(String sku);

    // Add method to check for slug existence if not already present
    boolean existsBySlugIgnoreCase(String slug);

    // Optional: Add findBySlugIgnoreCase if needed later
    // Optional<Product> findBySlugIgnoreCase(String slug);
}