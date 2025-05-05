package ptithcm.edu.pharmacy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp; // Import CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp;   // Import UpdateTimestamp
import java.math.BigDecimal;
import java.time.LocalDateTime; // Import LocalDateTime

@Data
@Entity
@Table(name = "Order_Items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY) // Consider LAZY fetch type
    @JoinColumn(name = "order_id", nullable = false)
    // @JsonBackReference // Remove this annotation
    @JsonIgnore // Replace with this annotation
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private BranchInventory inventory;

    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false)
    private BigDecimal priceAtPurchase;

    private BigDecimal subtotal;

    // Add Timestamps
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}