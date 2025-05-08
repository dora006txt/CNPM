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
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 if not authorized
        } catch (Exception e) {
            // Log the exception in a real application
            System.err.println("Error fetching order ID " + orderId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // --- End Add Endpoint ---

    // --- Add Endpoint for Order Cancellation ---
    // @PostMapping("/{orderId}/cancel") // Remove or comment out this line
    @PatchMapping("/{orderId}/cancel") // Change to PatchMapping
    public ResponseEntity<?> cancelOrder(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserIdFromPrincipal(userDetails);
        if (userId == null) {
            System.err.println("Could not determine user ID from principal for cancelOrder");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            OrderResponseDTO cancelledOrder = orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(cancelledOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error cancelling order ID " + orderId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
    // --- End Endpoint for Order Cancellation ---

    // --- Corrected helper method to extract User ID ---
    private Integer getUserIdFromPrincipal(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        String username = userDetails.getUsername(); // Username is the phone number
        if (username == null || username.isBlank()) {
            System.err.println("Username (phone number) from principal is null or blank.");
            return null;
        }

        // Fetch the User entity using the username (phone number)
        User user = userRepository.findByPhoneNumber(username) // Assuming findByPhoneNumber exists
                 .orElse(null); // Handle case where user not found
    
        if (user == null) {
            System.err.println("User not found in database for phone number: " + username);
            return null;
        }
    
        // Return the actual primary key ID
        return user.getUserId(); // Returns the user's ID
    }
}