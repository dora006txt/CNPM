package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Country;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    // Check if a country with the given name exists (case-insensitive check might be useful)
    // Rename 'Name' to 'CountryName' to match the entity field
    boolean existsByCountryNameIgnoreCase(String countryName);

    // Find by name (optional)
    // Rename 'Name' to 'CountryName' to match the entity field
    Optional<Country> findByCountryNameIgnoreCase(String countryName);
}