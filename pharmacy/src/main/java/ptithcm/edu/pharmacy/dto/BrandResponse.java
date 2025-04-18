package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Optional: for easy construction
public class BrandResponse {
    private Integer id;
    private String name;
    // Add other fields if needed
}