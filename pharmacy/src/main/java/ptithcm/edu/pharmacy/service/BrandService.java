package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.BrandRequest;
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.repository.BrandRepository;
// Add these imports
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException; // For delete check
import ptithcm.edu.pharmacy.repository.ProductRepository; // Assuming you have a ProductRepository

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired // Assuming you have a ProductRepository to check for associated products
    private ProductRepository productRepository;

    @Transactional
    public Brand createBrand(BrandRequest request) {
        // Validate input (basic check, @NotBlank handles blank name if validation
        // enabled)
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

    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + id));
    }

    @Transactional
    public Brand updateBrand(Integer id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + id));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand name cannot be empty.");
        }
        String trimmedName = request.getName().trim();

        // Check for duplicate name only if the name has changed
        if (!brand.getName().equalsIgnoreCase(trimmedName) && brandRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Brand with name '" + trimmedName + "' already exists.");
        }

        brand.setName(trimmedName);
        brand.setDescription(request.getDescription()); // Update description
        // Update other fields like logoUrl if added

        return brandRepository.save(brand);
    }

    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with ID: " + id));

        // Check if the brand is associated with any products
        // This requires ProductRepository and a method like existsByBrandId
        // For example:
        if (productRepository.existsById(id)) {
            throw new DataIntegrityViolationException(
                    "Cannot delete brand as it is associated with existing products.");
        }

        brandRepository.delete(brand);
    }
}