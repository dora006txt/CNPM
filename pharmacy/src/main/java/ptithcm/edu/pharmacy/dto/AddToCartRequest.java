package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class AddToCartRequest {
    // User ID will likely be retrieved from the security context,
    // but including it here for clarity in the request structure if needed directly.
    // private String userId;

    @NotNull(message = "Branch ID cannot be null")
    private Integer branchId;

    @NotNull(message = "Product ID cannot be null")
    private Integer productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}