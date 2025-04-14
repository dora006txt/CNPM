package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Payment_Types")
public class PaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_type_id")
    private Integer paymentTypeId;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}