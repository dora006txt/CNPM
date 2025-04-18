package ptithcm.edu.pharmacy.service;

import jakarta.persistence.EntityNotFoundException; // Import specific exception
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.ManufacturerRequest;
import ptithcm.edu.pharmacy.entity.Country;
import ptithcm.edu.pharmacy.entity.Manufacturer;
import ptithcm.edu.pharmacy.repository.CountryRepository;
import ptithcm.edu.pharmacy.repository.ManufacturerRepository;

@Service
public class ManufacturerService {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private CountryRepository countryRepository; // Inject CountryRepository to find the country

    @Transactional
    public Manufacturer createManufacturer(ManufacturerRequest request) {
        // Validate input (basic checks, @NotBlank/@NotNull handle others if validation enabled)
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer name cannot be empty.");
        }
        if (request.getCountryId() == null) {
            throw new IllegalArgumentException("Country ID must be provided.");
        }

        String trimmedName = request.getName().trim();

        // Check for duplicate name (case-insensitive)
        if (manufacturerRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Manufacturer with name '" + trimmedName + "' already exists.");
        }

        // Find the Country entity by the provided ID
        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ID: " + request.getCountryId()));

        // Create and populate the new Manufacturer entity
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(trimmedName);
        manufacturer.setCountry(country); // Set the fetched Country object

        // Save the new manufacturer
        return manufacturerRepository.save(manufacturer);
    }

    // Optional: Add methods for getting, updating, deleting manufacturers later
}