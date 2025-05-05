package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
// import ptithcm.edu.pharmacy.dto.OrderRequestDTO; // No longer needed
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import java.util.List; // Import List

public interface OrderService {

    OrderResponseDTO createOrderFromCart(CreateOrderRequestDTO createOrderRequestDTO, Integer userId);

    // --- Add these methods ---
    /**
     * Find all orders placed by a specific user.
     * @param userId The ID of the user.
     * @return A list of orders for the user.
     */
    List<OrderResponseDTO> findOrdersByUserId(Integer userId);

    /**
     * Find a specific order by its ID.
     * Includes checks for ownership or admin privileges (handled in controller/service).
     * @param orderId The ID of the order.
     * @param userId The ID of the user requesting the order (for ownership check).
     * @return The order details.
     * @throws jakarta.persistence.EntityNotFoundException if the order is not found.
     * @throws org.springframework.security.access.AccessDeniedException if the user is not authorized.
     */
    OrderResponseDTO findOrderById(Integer orderId, Integer userId);
    // --- End of added methods ---

    // TODO: Add other order-related method signatures (e.g., updateOrderStatus for admin)
}