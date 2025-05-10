package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesStatsDTO {
    private Integer productId;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenueFromProduct;
}