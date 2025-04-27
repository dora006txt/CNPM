package ptithcm.edu.pharmacy.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j for logging
import org.springframework.dao.DataIntegrityViolationException; // Import specific exception
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.AddToCartRequest;
import ptithcm.edu.pharmacy.dto.ShoppingCartDTO;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.UserRepository;
import ptithcm.edu.pharmacy.service.ShoppingCartService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;


@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j // Add Lombok logging annotation
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final UserRepository userRepository;

    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            log.warn("Attempt to add item to cart without authentication.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be logged in.");
        }
        String username = userDetails.getUsername();
        log.info("Received request to add item to cart for user: {}", username);
        log.debug("Request details: BranchId={}, ProductId={}, Quantity={}", request.getBranchId(), request.getProductId(), request.getQuantity());

        try {
            log.debug("Attempting to find user by username/phone: {}", username);
            User user = userRepository.findByPhoneNumber(username) // Or findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found for username/phone: {}", username);
                        return new EntityNotFoundException("User not found with username/phone: " + username);
                    });

            Integer userId = user.getUserId();
            log.debug("User found with ID: {}. Calling shoppingCartService.addItemToCart", userId);

            ShoppingCartDTO updatedCart = shoppingCartService.addItemToCart(userId, request);
            log.info("Successfully added/updated item for user ID: {}", userId);
            return ResponseEntity.ok(updatedCart);

        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException while adding item for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientStockException e) {
            log.warn("InsufficientStockException while adding item for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) { // Catch specific DB constraint errors
            log.error("DataIntegrityViolationException while adding item for user {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not add item due to a data conflict (e.g., constraint violation).");
        } catch (Exception e) {
            // Log the full stack trace for unexpected errors
            log.error("Unexpected error adding item to cart for user {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while adding item to cart.");
        }
    }

    // Add other endpoints (GET /api/v1/cart, DELETE /api/v1/cart/items/{itemId}, etc.) later
}