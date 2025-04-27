package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ShoppingCartItemDTO {
    private Integer cartItemId;
    private Integer inventoryId; // ID of the specific BranchInventory item
    private ProductCartItemDTO product; // Details of the product being added
    private Integer quantity;
    private LocalDateTime addedAt;
    // Add price per item or total price if needed
}