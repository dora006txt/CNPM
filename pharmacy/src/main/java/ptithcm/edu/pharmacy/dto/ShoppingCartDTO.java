package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDTO {
    private Integer cartId;
    private Integer userId;
    private Integer branchId; // Keep branch ID
    private String branchName; // Add branch name for display
    private List<ShoppingCartItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // private Double totalPrice; // Optional: if calculated on backend
}