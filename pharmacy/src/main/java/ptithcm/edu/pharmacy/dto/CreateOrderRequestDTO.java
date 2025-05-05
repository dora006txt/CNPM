package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class CreateOrderRequestDTO {
    private Integer branchId; // The branch where the order is placed/fulfilled
    private Integer shippingMethodId;
    private Integer paymentTypeId;
    private String notes; // Optional notes from the customer
    private Boolean requiresConsultation = false; // Default to false if not provided
    private String shippingAddress; // Optional, can override user's default
}