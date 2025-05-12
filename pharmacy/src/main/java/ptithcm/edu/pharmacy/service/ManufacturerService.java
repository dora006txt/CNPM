package ptithcm.edu.pharmacy.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.ManufacturerRequest;
import ptithcm.edu.pharmacy.entity.Country;
import ptithcm.edu.pharmacy.entity.Manufacturer;
import ptithcm.edu.pharmacy.repository.CountryRepository;
import ptithcm.edu.pharmacy.repository.ManufacturerRepository;

import java.util.List; // Added import

@Service
public class ManufacturerService {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Transactional
    public Manufacturer createManufacturer(ManufacturerRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer name cannot be empty.");
        }
        if (request.getCountryId() == null) {
            throw new IllegalArgumentException("Country ID must be provided.");
        }

        String trimmedName = request.getName().trim();

        if (manufacturerRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Manufacturer with name '" + trimmedName + "' already exists.");
        }

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ID: " + request.getCountryId()));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(trimmedName);
        manufacturer.setCountry(country);

        return manufacturerRepository.save(manufacturer);
    }

    @Transactional(readOnly = true)
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Manufacturer getManufacturerById(Integer id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found with ID: " + id));
    }

    @Transactional
    public Manufacturer updateManufacturer(Integer id, ManufacturerRequest request) {
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found with ID: " + id));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Manufacturer name cannot be empty.");
        }
        String trimmedName = request.getName().trim();

        // Check for duplicate name only if the name has changed
        if (!manufacturer.getName().equalsIgnoreCase(trimmedName) && manufacturerRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Manufacturer with name '" + trimmedName + "' already exists.");
        }

        manufacturer.setName(trimmedName);

        if (request.getCountryId() != null) {
            // Only update country if a new countryId is provided and it's different
            if (manufacturer.getCountry() == null || !manufacturer.getCountry().getCountryId().equals(request.getCountryId())) {
                Country country = countryRepository.findById(request.getCountryId())
                        .orElseThrow(() -> new EntityNotFoundException("Country not found with ID: " + request.getCountryId()));
                manufacturer.setCountry(country);
            }
        } else {
             throw new IllegalArgumentException("Country ID must be provided.");
        }


        return manufacturerRepository.save(manufacturer);
    }

    @Transactional
    public void deleteManufacturer(Integer id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new EntityNotFoundException("Manufacturer not found with ID: " + id);
        }
        // Add any necessary checks here, e.g., if the manufacturer is associated with products
        // For example:
        // if (productRepository.existsByManufacturerId(id)) {
        //     throw new DataIntegrityViolationException("Cannot delete manufacturer as it is associated with existing products.");
        // }
        manufacturerRepository.deleteById(id);
    }
}