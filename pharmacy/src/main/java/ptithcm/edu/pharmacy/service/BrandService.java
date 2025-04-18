package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.BrandRequest;
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.repository.BrandRepository;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public Brand createBrand(BrandRequest request) {
        // Validate input (basic check, @NotBlank handles blank name if validation enabled)
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be empty.");
        }

        String trimmedName = request.getName().trim();

        // Check for duplicate name (case-insensitive)
        if (brandRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Brand with name '" + trimmedName + "' already exists.");
        }

        // Create and populate the new Brand entity
        Brand brand = new Brand();
        brand.setName(trimmedName);
        brand.setDescription(request.getDescription()); // Set description (can be null)
        // Set other fields like logoUrl if added

        // Save the new brand
        return brandRepository.save(brand);
    }

    // Optional: Add methods for getting, updating, deleting brands later
}