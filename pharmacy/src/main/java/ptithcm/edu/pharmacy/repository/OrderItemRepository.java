package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Find all items belonging to a specific order
    List<OrderItem> findByOrder_OrderId(Integer orderId);

    // Find all order items for a specific product (e.g., for sales analysis)
    List<OrderItem> findByProduct_Id(Integer productId);

    // Find all order items related to a specific inventory record
    List<OrderItem> findByInventory_InventoryId(Integer inventoryId);

    // Add other custom query methods as needed
}