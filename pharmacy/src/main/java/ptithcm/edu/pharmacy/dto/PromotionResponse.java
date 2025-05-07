package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import ptithcm.edu.pharmacy.entity.ApplicableScope;
import ptithcm.edu.pharmacy.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PromotionResponse {
    private Integer promotionId;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderValue;
    private Integer usageLimitPerCustomer;
    private Integer totalUsageLimit;
    private Integer totalUsedCount;
    private ApplicableScope applicableScope;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<CategoryDTO> categories; // Using a simplified CategoryDTO
    private Set<ProductDTO> products;    // Using a simplified ProductDTO
    private Set<SimpleBranchDTO> branches;      // Using a simplified SimpleBranchDTO
}