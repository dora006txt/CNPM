package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateOrderRequestDTO {

    @NotNull(message = "Shipping method ID cannot be null")
    private Integer shippingMethodId;

    @NotNull(message = "Payment type ID cannot be null")
    private Integer paymentTypeId;

    private String shippingAddress;

    private String notes;

    private String promotionCode;
}