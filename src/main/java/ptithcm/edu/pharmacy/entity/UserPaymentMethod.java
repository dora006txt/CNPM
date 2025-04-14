package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "User_Payment_Methods")
public class UserPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_payment_method_id")
    private Integer userPaymentMethodId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    private String provider;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}