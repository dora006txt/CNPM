package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.CountryRequest;
import ptithcm.edu.pharmacy.entity.Country;
import ptithcm.edu.pharmacy.repository.CountryRepository;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Transactional
    public Country createCountry(CountryRequest countryRequest) {
        // Validate input (basic check, @NotBlank handles blank names/codes if validation is enabled)
        if (countryRequest.getCountryName() == null || countryRequest.getCountryName().trim().isEmpty()) {
             throw new IllegalArgumentException("Country name cannot be empty.");
        }
        if (countryRequest.getCountryCode() == null || countryRequest.getCountryCode().trim().isEmpty()) {
             throw new IllegalArgumentException("Country code cannot be empty.");
        }
        // Optional: Add check for code length if not using validation annotations
        // if (countryRequest.getCountryCode().trim().length() != 2) {
        //     throw new IllegalArgumentException("Country code must be 2 characters.");
        // }

        String trimmedName = countryRequest.getCountryName().trim();
        String trimmedCode = countryRequest.getCountryCode().trim().toUpperCase(); // Store codes consistently (e.g., uppercase)

        // Check for duplicate name (case-insensitive) - Use updated repository method
        if (countryRepository.existsByCountryNameIgnoreCase(trimmedName)) {
            throw new RuntimeException("Country with name '" + trimmedName + "' already exists.");
        }
        // Optional: Check for duplicate code if needed (assuming code should also be unique)
        // if (countryRepository.existsByCountryCodeIgnoreCase(trimmedCode)) { // Need to add this method to repo if check is desired
        //    throw new RuntimeException("Country with code '" + trimmedCode + "' already exists.");
        // }


        Country country = new Country();
        // Use the correct setter from the entity
        country.setCountryName(trimmedName);
        country.setCountryCode(trimmedCode);

        return countryRepository.save(country);
    }

    // Optional: Add methods for getting, updating, deleting countries later
}