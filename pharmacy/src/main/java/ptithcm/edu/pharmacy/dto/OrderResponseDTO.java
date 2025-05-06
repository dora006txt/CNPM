package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import ptithcm.edu.pharmacy.entity.ConsultationStatus;
import ptithcm.edu.pharmacy.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Integer orderId;
    private String orderCode;
    private Integer userId;
    private String userFullName; // Example: Add user's name
    private Integer branchId;
    private String branchName; // Example: Add branch name
    private LocalDateTime orderDate;
    private Integer shippingMethodId;
    private String shippingMethodName; // Example: Add shipping method name
    private Integer orderStatusId;
    private String orderStatusName; // Example: Add status description
    private BigDecimal subtotalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private Integer paymentTypeId;
    private String paymentTypeName; // Example: Add payment type name
    private PaymentStatus paymentStatus;
    private String notes;
    private Boolean requiresConsultation;
    private Integer assignedStaffId; // ID of staff if assigned
    private String assignedStaffName; // Example: Add staff name
    private ConsultationStatus consultationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt; // Keep this one
    private Boolean isCancellable; // To indicate if the order can be cancelled
    private List<OrderItemResponseDTO> orderItems;
    private String shippingAddress; // Add the user's address here
    // private LocalDateTime cancelled_at; // Remove this duplicate field
}