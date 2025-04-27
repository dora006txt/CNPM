package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal; // Assuming price is BigDecimal

@Data
@Builder
public class ProductCartItemDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price; // Add price if available in BranchInventory or Product
    private String unit; // e.g., "Box", "Bottle"
}