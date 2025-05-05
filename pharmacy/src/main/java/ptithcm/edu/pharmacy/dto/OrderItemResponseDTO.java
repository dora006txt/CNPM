package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {
    private Integer orderItemId;
    private Integer productId;
    private String productName; // Include product name for convenience
    private String productImageUrl; // Include image URL
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
}