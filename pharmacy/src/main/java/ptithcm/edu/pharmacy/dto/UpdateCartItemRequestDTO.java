package ptithcm.edu.pharmacy.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCartItemRequestDTO {

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}