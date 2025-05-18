package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrderFromCart(CreateOrderRequestDTO createOrderRequestDTO, Integer userId);
    List<OrderResponseDTO> findOrdersByUserId(Integer userId);
    OrderResponseDTO findOrderById(Integer orderId, Integer userId);
    OrderResponseDTO cancelOrder(Integer orderId, Integer userId);
}