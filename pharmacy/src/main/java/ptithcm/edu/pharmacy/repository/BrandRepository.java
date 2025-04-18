package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Brand;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> { // Assuming ID is Integer

    // Check if a brand with the given name exists (case-insensitive)
    boolean existsByNameIgnoreCase(String name);

    // Optional: Find by name if needed later
    Optional<Brand> findByNameIgnoreCase(String name);
}