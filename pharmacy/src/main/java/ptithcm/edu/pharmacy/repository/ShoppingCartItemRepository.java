package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.ShoppingCartItem;

import java.util.Optional;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Integer> {
    // Rename Inventory_Id to Inventory_InventoryId to match the field in BranchInventory
    Optional<ShoppingCartItem> findByCart_CartIdAndInventory_InventoryId(Integer cartId, Integer inventoryId);

    // You might also need methods like these (adjust as needed):
    // List<ShoppingCartItem> findByCart_CartId(Integer cartId);
    // void deleteByCart_CartIdAndInventory_InventoryId(Integer cartId, Integer inventoryId);
}