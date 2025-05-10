package ptithcm.edu.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByPeriodDTO {
    private String period; // e.g., "2023-10-26", "2023-10", "2023-Q4", "2023"
    private BigDecimal totalRevenue;
}