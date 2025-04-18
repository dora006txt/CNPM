package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Shipping_Methods")
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Integer methodId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "base_cost")
    private BigDecimal baseCost = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;
}