package ptithcm.edu.pharmacy.dto;

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
    private List<ShoppingCartItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}