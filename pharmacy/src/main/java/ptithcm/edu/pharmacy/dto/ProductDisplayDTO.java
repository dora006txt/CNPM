package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDisplayDTO {
    // Product Info
    private Integer productId;
    private String productName;
    private String description;
    private String imageUrl;
    private String categoryName; // Example: Added from Product's Category

    // Branch Inventory Info
    private Integer inventoryId;
    private Integer quantityOnHand;
    private BigDecimal price;
    private BigDecimal discountPrice; // Optional: Include if needed
    private String locationInStore; // Optional: Include if needed

    // Branch Info (Optional)
    private Integer branchId;
    private String branchName;
}