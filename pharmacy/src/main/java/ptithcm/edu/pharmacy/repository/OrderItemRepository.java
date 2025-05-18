package ptithcm.edu.pharmacy.repository;

// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable; // Remove this line
import org.springframework.data.domain.Pageable; // Add this line
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

        // Find all items belonging to a specific order
        List<OrderItem> findByOrder_OrderId(Integer orderId);

        // Find all order items for a specific product (e.g., for sales analysis)
        List<OrderItem> findByProduct_Id(Integer productId);

        // Find all order items related to a specific inventory record
        List<OrderItem> findByInventory_InventoryId(Integer inventoryId);

        @Query("SELECT od.product.id, SUM(od.quantity), SUM(od.quantity * od.priceAtPurchase) " +
                        "FROM OrderItem od JOIN od.order o " +
                        "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY od.product.id " + // Ensure grouping by product.id
                        "ORDER BY SUM(od.quantity * od.priceAtPurchase) DESC, SUM(od.quantity) DESC")
        List<Object[]> findTopSellingProductsByDateRange(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

    // --- New Method: Find Top Selling Product Aggregates ---
    /**
     * Retrieves aggregated data for top-selling products.
     * Returns a list of Object arrays, where each array contains:
     *  - Object[0]: Product ID (Integer)
     *  - Object[1]: Total quantity sold (Long)
     * The results are ordered by the total quantity sold in descending order.
     * @param pageable Pageable object to limit the number of results.
     * @return A list of Object[] representing top-selling product data.
     */
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi " +
           "WHERE oi.product IS NOT NULL " +
           "GROUP BY oi.product.id " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProductAggregates(Pageable pageable);
    // --- End New Method ---

        // Add other custom query methods as needed
}