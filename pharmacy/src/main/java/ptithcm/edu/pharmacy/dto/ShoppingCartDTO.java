package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ShoppingCartDTO {
    private Integer cartId;
    private Integer userId;
    private Integer branchId; // Add branchId
    private List<ShoppingCartItemDTO> cartItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}