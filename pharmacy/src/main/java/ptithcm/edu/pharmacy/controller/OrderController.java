package ptithcm.edu.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import ptithcm.edu.pharmacy.service.OrderService;
import ptithcm.edu.pharmacy.entity.User; // Import User entity
import ptithcm.edu.pharmacy.repository.UserRepository; // Import UserRepository
import java.util.List; // Import List

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

    // --- Add Endpoint to Get User's Orders ---
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = getUserIdFromPrincipal(userDetails);
        if (userId == null) {
            // Log this appropriately in a real application
            System.err.println("Could not determine user ID from principal for getMyOrders");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<OrderResponseDTO> orders = orderService.findOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    // --- End Add Endpoint ---


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


    // TODO: Add endpoints for GET /orders/{id}, PUT /orders/{id}/status, etc.

}