package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // Use BigDecimal for price if applicable
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemDTO {
    private Integer cartItemId;
    private Integer inventoryId; // ID from BranchInventory
    private Integer productId; // ID from Product
    private String productName; // Add product name
    private Integer quantity;
    private BigDecimal price; // Price per unit (use BigDecimal for currency)
    private String productImageUrl; // Add image URL
    private String unit; // Add unit for display (e.g., 'box', 'bottle')
    private LocalDateTime addedAt;
    private Integer branchId;
    private String branchName;
}