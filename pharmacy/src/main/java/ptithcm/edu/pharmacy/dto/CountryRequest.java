package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Import Size for length validation

@Data
public class CountryRequest {
    @NotBlank(message = "Country name cannot be blank")
    private String countryName; // Changed from 'name' to 'countryName'

    @NotBlank(message = "Country code cannot be blank")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters") // Add validation for code length
    private String countryCode;
}