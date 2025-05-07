package ptithcm.edu.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import ptithcm.edu.pharmacy.service.OrderService;
import ptithcm.edu.pharmacy.entity.User; // Import User entity
import ptithcm.edu.pharmacy.repository.UserRepository; // Import UserRepository
import java.util.List; // Import List
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.PatchMapping; // Ensure this import is present

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository; // Inject UserRepository

    @PostMapping("/from-cart")
    public ResponseEntity<OrderResponseDTO> createOrderFromCart(
            @RequestBody CreateOrderRequestDTO createOrderRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        Integer userId = getUserIdFromPrincipal(userDetails);
        if (userId == null) {
             // Consider throwing a more specific exception or logging
            System.err.println("Could not determine user ID from principal: " + (userDetails != null ? userDetails.getUsername() : "null"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OrderResponseDTO createdOrder = orderService.createOrderFromCart(createOrderRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    // --- Endpoint to Get User's Orders ---
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = getUserIdFromPrincipal(userDetails);
        if (userId == null) {
            System.err.println("Could not determine user ID from principal for getMyOrders");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<OrderResponseDTO> orders = orderService.findOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    // --- End Endpoint ---

    // --- Add Endpoint to Get Specific Order Details ---
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserIdFromPrincipal(userDetails);
        if (userId == null) {
            System.err.println("Could not determine user ID from principal for getOrderById");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            OrderResponseDTO order = orderService.findOrderById(orderId, userId);
            return ResponseEntity.ok(order);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedE