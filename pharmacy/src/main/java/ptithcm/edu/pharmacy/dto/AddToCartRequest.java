package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class AddToCartRequest {
    @NotNull(message = "Inventory ID cannot be null")
    private Integer inventoryId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}