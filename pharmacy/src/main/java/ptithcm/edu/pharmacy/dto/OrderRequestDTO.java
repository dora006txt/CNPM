package ptithcm.edu.pharmacy.dto;

import lombok.Data;  

import java.util.List;

@Data
public class OrderRequestDTO {
    // No userId here, it should come from the authenticated user context (e.g., path variable or security principal)
    private Integer branchId;
    private Integer shippingMethodId;
    private Integer paymentTypeId;
    private String notes;
    private Boolean requiresConsultation = false; // Default to false
    private List<OrderItemRequestDTO> items;
    private String shippingAddress; // Optional: If different from user's default
}