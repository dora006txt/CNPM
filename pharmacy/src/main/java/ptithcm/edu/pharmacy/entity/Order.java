package ptithcm.edu.pharmacy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter; // Using specific annotations instead of @Data
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

// Consider replacing @Data with specific annotations to avoid potential issues
@Getter
@Setter
@ToString(exclude = {"user", "branch", "shippingMethod", "orderStatus", "paymentType", "assignedStaff", "orderItems"}) // Exclude relationships from toString
@EqualsAndHashCode(exclude = {"user", "branch", "shippingMethod", "orderStatus", "paymentType", "assignedStaff", "orderItems"}) // Exclude relationships from equals/hashCode
@Entity
@Table(name = "Orders") // Changed table name to "Orders" (plural)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "shipping_address", length = 500) // Adjust length as needed
    private String shippingAddress;

    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "subtotal_amount", nullable = false)
    private BigDecimal subtotalAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(columnDefinition = "TEXT") // Use TEXT for potentially longer notes
    private String notes;

    @Column(name = "requires_consultation")
    private Boolean requiresConsultation = false; // Default to false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private Staff assignedStaff; // Optional staff assignment

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_status")
    private ConsultationStatus consultationStatus;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp // Add annotation
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    // Change FetchType.EAGER to FetchType.LAZY
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Changed to LAZY
    @JsonManagedReference
    private Set<OrderItem> orderItems;
}