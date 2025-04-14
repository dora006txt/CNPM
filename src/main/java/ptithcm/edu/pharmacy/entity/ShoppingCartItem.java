package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Shopping_Cart_Items")
public class ShoppingCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private BranchInventory inventory;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "added_at")
    private LocalDateTime addedAt;
}