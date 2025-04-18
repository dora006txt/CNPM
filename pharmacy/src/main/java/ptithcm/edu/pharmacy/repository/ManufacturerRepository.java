package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Manufacturer;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Integer> { // Assuming ID is Integer

    // Check if a manufacturer with the given name exists (case-insensitive)
    boolean existsByNameIgnoreCase(String name);

    // Optional: Find by name if needed later
    Optional<Manufacturer> findByNameIgnoreCase(String name);
}