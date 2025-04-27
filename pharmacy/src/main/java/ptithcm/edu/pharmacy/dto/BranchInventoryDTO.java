package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BranchInventoryDTO {
    private Integer inventoryId;
    private Integer branchId; // ID of the related branch
    private Integer productId; // ID of the related product
    private Integer quantityOnHand;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private LocalDate expiryDate;
    private String batchNumber;
    private String locationInStore;
    private LocalDateTime lastUpdated;
}