package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.ShoppingCartItem;

import java.util.Optional;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Integer> {
    // Optional: Add custom queries if needed, e.g., finding by cart ID and item ID
    //Optional<ShoppingCartItem> findByCart_CartIdAndCartItemId(Integer cartId, Integer cartItemId);

    // Find item by ID and ensure it belongs to the correct user's cart
    Optional<ShoppingCartItem> findByCartItemIdAndCart_User_UserId(Integer cartItemId, Integer userId);
}