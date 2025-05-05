package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTypeDTO {
    private Integer paymentTypeId;
    private String typeName;
    private String description;
    private Boolean isActive;
}