package ptithcm.edu.pharmacy.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j for logging
import org.springframework.dao.DataIntegrityViolationException; // Import specific exception
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.AddToCartRequest;
import ptithcm.edu.pharmacy.dto.ShoppingCartDTO;
import ptithcm.edu.pharmacy.dto.UpdateCartItemRequestDTO;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.UserRepository;
import ptithcm.edu.pharmacy.service.ShoppingCartService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
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
        // Sửa dòng log dưới đây
        log.debug("Request details: InventoryId={}, Quantity={}", request.getInventoryId(), request.getQuantity());

        try {
            log.debug("Attempting to find user by username/phone: {}", username);
            User user = userRepository.findByPhoneNumber(username) // Or findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found for username/phone: {}", username);
                        return new EntityNotFoundException("User not found with username/phone: " + username);
                    });

            Integer userId = user.getUserId();
            log.debug("User found with ID: {}. Calling shoppingCartService.addItemToCart", userId);

            // This call now matches the updated interface and existing implementation
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
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Could not add item due to a data conflict (e.g., constraint violation).");
        } catch (Exception e) {
            // Log the full stack trace for unexpected errors
            log.error("Unexpected error adding item to cart for user {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while adding item to cart.");
        }
    }

    @GetMapping
    public ResponseEntity<ShoppingCartDTO> getUserCart() { // Renamed and changed return type
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {
                log.warn("User not authenticated trying to view cart.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String username = authentication.getName(); // This is the phone number

            User user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + username));
            Integer userId = user.getUserId();
            log.info("Fetching cart for userId: {}", userId);

            Optional<ShoppingCartDTO> cartDTOOptional = shoppingCartService.getCartByUserId(userId);

            if (cartDTOOptional.isPresent()) {
                log.info("Returning cart for userId: {}", userId);
                return ResponseEntity.ok(cartDTOOptional.get());
            } else {
                log.info("No cart found for userId: {}", userId);
                return ResponseEntity.noContent().build();
            }

        } catch (UsernameNotFoundException e) {
            log.error("Authentication error while fetching cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error fetching cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be logged in.");
        }
        String username = userDetails.getUsername(); // Phone number
        log.info("Received request to update cart item ID: {} for user: {} with quantity: {}", cartItemId, username,
                request.getQuantity());

        try {
            User user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + username));
            Integer userId = user.getUserId();

            ShoppingCartDTO updatedCart = shoppingCartService.updateItemQuantity(userId, cartItemId,
                    request.getQuantity());
            log.info("Successfully updated item ID: {} for user ID: {}", cartItemId, userId);
            return ResponseEntity.ok(updatedCart);

        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException while updating item for user {}: {}", username, e.getMessage());
            // Could be user not found OR cart item not found/not belonging to user
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientStockException e) {
            log.warn("InsufficientStockException while updating item for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Catch potential validation errors from service
            log.warn("IllegalArgumentException while updating item for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating cart item ID {} for user {}: {}", cartItemId, username, e.getMessage(),
                    e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Integer cartItemId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be logged in.");
        }
        String username = userDetails.getUsername(); // Phone number
        log.info("Received request to remove cart item ID: {} for user: {}", cartItemId, username);

        try {
            User user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + username));
            Integer userId = user.getUserId();

            ShoppingCartDTO updatedCart = shoppingCartService.removeItemFromCart(userId, cartItemId);
            log.info("Successfully removed item ID: {} for user ID: {}", cartItemId, userId);
            // Decide what to return: the updated cart, or just status OK if cart might be
            // empty/deleted
            if (updatedCart == null || (updatedCart.getItems() != null && updatedCart.getItems().isEmpty())) {
                // Optionally return 204 No Content if the cart is now empty or deleted
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(updatedCart);

        } catch (EntityNotFoundException e) {
            log.warn("EntityNotFoundException while removing item for user {}: {}", username, e.getMessage());
            // Could be user not found OR cart item not found/not belonging to user
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Catch potential validation errors from service
            log.warn("IllegalArgumentException while removing item for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error removing cart item ID {} for user {}: {}", cartItemId, username, e.getMessage(),
                    e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}