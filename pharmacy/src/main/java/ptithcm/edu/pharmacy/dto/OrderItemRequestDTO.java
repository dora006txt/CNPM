package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Integer productId;
    private Integer quantity;
    // No need for inventoryId here, it should be determined based on the branch in OrderRequestDTO
}