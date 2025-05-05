package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*; // Ensure all necessary Lombok imports are present

import java.time.LocalDateTime;

@Getter // Keep Getter
@Setter // Keep Setter
@NoArgsConstructor // Keep NoArgsConstructor
@AllArgsConstructor // Keep AllArgsConstructor
@Entity
@Table(name = "Shopping_Cart_Items")
// Add exclude = "cart" to break the recursion cycle
@EqualsAndHashCode(exclude = "cart")
public class ShoppingCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY is often good practice here
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY is often good practice here
    @JoinColumn(name = "inventory_id", nullable = false)
    private BranchInventory inventory;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    // If you are NOT using Lombok @EqualsAndHashCode, you would manually implement like this:
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem that = (ShoppingCartItem) o;
        // DO NOT include cart in equals comparison if excluded from hashCode
        return Objects.equals(cartItemId, that.cartItemId) &&
               Objects.equals(inventory, that.inventory) && // Compare inventory if needed
               Objects.equals(quantity, that.quantity) &&
               Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        // DO NOT include cart.hashCode()
        return Objects.hash(cartItemId, inventory, quantity, addedAt);
    }
    */
}