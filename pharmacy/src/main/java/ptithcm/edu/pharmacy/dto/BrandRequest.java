package ptithcm.edu.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {

    @NotBlank(message = "Brand name cannot be blank")
    private String name;

    private String description; // Optional

    // Add other fields from the request if needed (e.g., logoUrl)
    //private String logoUrl;
}