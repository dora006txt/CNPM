package ptithcm.edu.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManufacturerRequest {

    @NotBlank(message = "Manufacturer name cannot be blank")
    private String name;

    @NotNull(message = "Country ID cannot be null")
    private Integer countryId; // ID of the country the manufacturer belongs to
}