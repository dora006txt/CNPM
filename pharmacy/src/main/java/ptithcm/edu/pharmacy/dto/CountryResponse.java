package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Optional: for easy construction
public class CountryResponse {
    private Integer id;
    private String countryCode;
    private String countryName;
}