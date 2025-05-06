package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.OrderStatus;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    /**
     * Finds an OrderStatus entity by its unique status name.
     * This is used to fetch the initial "PENDING" status when creating an order.
     *
     * @param statusName The name of the status (e.g., "PENDING", "PROCESSING", "SHIPPED").
     * @return An Optional containing the OrderStatus if found, otherwise empty.
     */
    Optional<OrderStatus> findByStatusNameIgnoreCase(String statusName);

    // Add other custom query methods if needed in the future
}