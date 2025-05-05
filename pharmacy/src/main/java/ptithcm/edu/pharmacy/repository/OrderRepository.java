package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find orders by user ID
    List<Order> findByUser_UserId(Integer userId);

    // Find an order by its unique code
    Optional<Order> findByOrderCode(String orderCode);

    // Add other custom query methods as needed
    // Example: Find orders by status
    // List<Order> findByOrderStatus_StatusName(String statusName);

    // Example: Find orders for a specific branch
    // List<Order> findByBranch_BranchId(Integer branchId);
}