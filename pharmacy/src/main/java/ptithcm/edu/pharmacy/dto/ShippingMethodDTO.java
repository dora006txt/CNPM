package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShippingMethodDTO {
    private Integer methodId;
    private String name;
    private String description;
    private BigDecimal baseCost;
    private Boolean isActive;
}