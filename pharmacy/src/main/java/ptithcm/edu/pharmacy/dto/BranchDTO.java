package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BranchDTO {
    private Integer branchId;
    private String name;
    private String address;
    private String phoneNumber;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String operatingHours;
    private Boolean isActive;
    // Add other fields as needed for the display list
}