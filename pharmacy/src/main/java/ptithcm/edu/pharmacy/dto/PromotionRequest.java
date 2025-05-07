package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import ptithcm.edu.pharmacy.entity.ApplicableScope;
import ptithcm.edu.pharmacy.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PromotionRequest {
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
    private ApplicableScope applicableScope;
    private Boolean isActive;
    private Set<Integer> categoryIds; // IDs of applicable categories
    private Set<Integer> productIds;  // IDs of applicable products
    private Set<Integer> branchIds;   // IDs of applicable branches
}