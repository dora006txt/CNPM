package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Optional: for easy construction
public class ManufacturerResponse {
    private Integer id;
    private String name;
    private CountryResponse country; // Embed CountryResponse
}