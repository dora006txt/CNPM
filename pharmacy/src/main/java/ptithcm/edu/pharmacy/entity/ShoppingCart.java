package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Shopping_Carts", uniqueConstraints = {
    // Optional: Add a unique constraint if a user should only have ONE cart per branch
    @UniqueConstraint(columnNames = {"user_id", "branch_id"})
})
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @OneToOne // Changed from OneToOne to ManyToOne if User can have multiple carts
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Add reference to the Branch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false) // Cart must belong to a branch
    private Branch branch;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Add CascadeType.ALL and orphanRemoval = true
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ShoppingCartItem> cartItems = new HashSet<>();
}