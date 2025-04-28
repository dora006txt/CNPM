package ptithcm.edu.pharmacy.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.*;
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.entity.Branch; // Import Branch
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.repository.ShoppingCartItemRepository; // Add this import
import ptithcm.edu.pharmacy.service.ShoppingCartService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;

import java.time.LocalDateTime;
import java.util.List; // Make sure List is imported
import java.util.Optional;
import java.util.Collections; // Add this import
import java.util.stream.Collectors; // Add this import

@Service
@RequiredArgsConstructor
@Slf4j // Add Lombok logging annotation
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final BranchInventoryRepository inventoryRepository;
    private final ShoppingCartItemRepository cartItemRepository; // Inject Cart Item Repo

    @Override
    @Transactional
    public ShoppingCartDTO addItemToCart(Integer userId, AddToCartRequest request) {
        log.info("addItemToCart started for userId: {}, request: {}", userId, request);

        // 1. Find the User
        log.debug("Finding user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });
        log.debug("User found: {}", user.getFullName());

        // 2. Find the specific BranchInventory item to get product and BRANCH info
        log.debug("Finding inventory for branchId: {} and productId: {}", request.getBranchId(), request.getProductId());
        BranchInventory inventoryItem = inventoryRepository.findByBranch_BranchIdAndProduct_Id(request.getBranchId(), request.getProductId())
                .orElseThrow(() -> {
                    log.warn("Inventory item not found for branchId: {} and productId: {}", request.getBranchId(), request.getProductId());
                    return new EntityNotFoundException(
                            "Product with ID " + request.getProductId() + " not found in branch with ID " + request.getBranchId());
                });
        log.debug("Inventory item found with ID: {}. Belongs to Branch ID: {}", inventoryItem.getInventoryId(), inventoryItem.getBranch().getBranchId());
        Branch itemBranch = inventoryItem.getBranch(); // Get the branch of the item being added

        // 3. Check stock for the requested quantity initially
        log.debug("Checking initial stock for inventoryId: {}. Available: {}, Requested: {}", inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), request.getQuantity());
        if (inventoryItem.getQuantityOnHand() < request.getQuantity()) {
            log.warn("Insufficient stock for inventoryId: {}. Available: {}, Requested: {}", inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), request.getQuantity());
            throw new InsufficientStockException("Insufficient stock for product ID " + request.getProductId() +
                    " at branch ID " + request.getBranchId() + ". Available: " + inventoryItem.getQuantityOnHand());
        }

        // 4. Find the user's ShoppingCart FOR THE SPECIFIC BRANCH
        log.debug("Finding cart for userId: {} AND branchId: {}", userId, itemBranch.getBranchId());
        Optional<ShoppingCart> cartOptional = cartRepository.findByUser_UserIdAndBranch_BranchId(userId, itemBranch.getBranchId());

        ShoppingCart cart;
        boolean isNewCart = false;

        if (cartOptional.isPresent()) {
            // 4a. Cart for this branch exists, use it
            cart = cartOptional.get();
            log.debug("Existing cart found for user {} and branch {}. Cart ID: {}", userId, itemBranch.getBranchId(), cart.getCartId());
        } else {
            // 4b. No cart for this branch exists, create a new one
            log.info("No existing cart found for userId: {} and branchId: {}. Creating new cart.", userId, itemBranch.getBranchId());
            cart = createNewCart(user, itemBranch); // Pass the branch
            isNewCart = true;
            log.debug("Created new cart object (not yet saved).");
        }

        // 5. Check if the item (inventoryId) is already in the selected cart
        log.debug("Checking if inventory item ID: {} exists in cart ID: {}", inventoryItem.getInventoryId(), cart.getCartId());
        final Integer inventoryIdToCheck = inventoryItem.getInventoryId();
        Optional<ShoppingCartItem> existingCartItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getInventory() != null && item.getInventory().getInventoryId().equals(inventoryIdToCheck))
                .findFirst();

        if (existingCartItemOpt.isPresent()) {
            // 5a. Item exists in cart, update quantity
            ShoppingCartItem existingCartItem = existingCartItemOpt.get();
            log.debug("Inventory item ID: {} found in cart (CartItemID: {}). Updating quantity.", inventoryIdToCheck, existingCartItem.getCartItemId());
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            log.debug("New quantity calculation: {} (current) + {} (request) = {}", existingCartItem.getQuantity(), request.getQuantity(), newQuantity);

            // 5b. Re-check stock for the TOTAL quantity needed
            if (inventoryItem.getQuantityOnHand() < newQuantity) {
                 log.warn("Insufficient stock for inventoryId: {} for updated quantity. Available: {}, Requested total: {}", inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), newQuantity);
                throw new InsufficientStockException("Insufficient stock for product ID " + request.getProductId() +
                        " at branch ID " + request.getBranchId() + ". Requested total: " + newQuantity +
                        ", Available: " + inventoryItem.getQuantityOnHand());
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setAddedAt(LocalDateTime.now());
            log.debug("Updated quantity for CartItemID: {} to {}", existingCartItem.getCartItemId(), newQuantity);
        } else {
            // 5c. Item does not exist in cart, add new item
            log.debug("Inventory item ID: {} not found in cart. Creating new ShoppingCartItem.", inventoryIdToCheck);
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setCart(cart); // Link item to the cart (important for cascade)
            newItem.setInventory(inventoryItem);
            newItem.setQuantity(request.getQuantity());
            newItem.setAddedAt(LocalDateTime.now());
            cart.getCartItems().add(newItem); // Add item to the cart's collection
            log.debug("Added new ShoppingCartItem to cart's item collection.");
        }

        // 6. Update cart timestamp and save (handles both new and existing carts)
        cart.setUpdatedAt(LocalDateTime.now());

        // *** ADD DETAILED LOGGING BEFORE SAVE ***
        log.debug(">>> Preparing to save cart <<<");
        log.debug("Cart ID (before save): {}", cart.getCartId()); // Will be null for new carts
        log.debug("Cart User ID: {}", (cart.getUser() != null) ? cart.getUser().getUserId() : "null");
        log.debug("Cart Branch ID: {}", (cart.getBranch() != null) ? cart.getBranch().getBranchId() : "null");
        log.debug("Is New Cart flag: {}", isNewCart);
        log.debug("Number of items in cart collection: {}", cart.getCartItems() != null ? cart.getCartItems().size() : "null collection");
        if (cart.getCartItems() != null) {
            cart.getCartItems().forEach(item -> {
                log.debug("  - Item (hashCode: {}): Quantity={}, InventoryID={}, AddedAt={}",
                          item.hashCode(),
                          item.getQuantity(),
                          (item.getInventory() != null) ? item.getInventory().getInventoryId() : "null",
                          item.getAddedAt());
                // Check if the item's cart reference is correctly set back to the parent cart object
                log.debug("    Item's cart reference matches parent? {}", item.getCart() == cart);
            });
        }
        log.debug(">>> Proceeding with cartRepository.save(cart) <<<");
        // *** END DETAILED LOGGING ***

        ShoppingCart savedCart = cartRepository.save(cart); // Cascades save/update to items
        log.info("Successfully saved cart ID: {}", savedCart.getCartId());

        // Log saved state (optional, keep if helpful)
        logSavedCartState(savedCart);

        // 7. Map to DTO and return
        log.debug("Mapping saved cart to DTO.");
        return mapToShoppingCartDTO(savedCart);
    }

    @Override
    @Transactional(readOnly = true) // Use read-only transaction for fetching data
    public List<ShoppingCartDTO> getCartsByUserId(Integer userId) {
        log.debug("Fetching all carts for userId: {}", userId);
        List<ShoppingCart> carts = cartRepository.findByUser_UserId(userId);

        if (carts.isEmpty()) {
            log.debug("No carts found for userId: {}", userId);
            return Collections.emptyList(); // Return empty list if no carts found
        }

        log.debug("Found {} carts for userId: {}. Mapping to DTOs.", carts.size(), userId);
        // Map each ShoppingCart entity to its DTO representation
        return carts.stream()
                .map(this::mapToShoppingCartDTO) // Use the existing helper method
                .collect(Collectors.toList());
    }

    // Modify createNewCart to accept Branch
    private ShoppingCart createNewCart(User user, Branch branch) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUser(user);
        newCart.setBranch(branch); // Set the branch for the new cart
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());
        newCart.setCartItems(new java.util.HashSet<>());
        log.debug("Created new ShoppingCart object for user ID: {} and branch ID: {}", user.getUserId(), branch.getBranchId());
        return newCart;
    }

    // Helper to log saved cart state (optional)
    private void logSavedCartState(ShoppingCart savedCart) {
        log.debug("Inspecting savedCart after save. Cart ID: {}", savedCart.getCartId());
        if (savedCart.getCartItems() != null) {
            log.debug("Saved cart has {} items.", savedCart.getCartItems().size());
            savedCart.getCartItems().forEach(item -> {
                Integer itemId = item.getCartItemId();
                Integer itemQty = item.getQuantity();
                Integer invId = (item.getInventory() != null) ? item.getInventory().getInventoryId() : null;
                Integer branchId = (item.getInventory() != null && item.getInventory().getBranch() != null) ? item.getInventory().getBranch().getBranchId() : null;
                Integer prodId = (item.getInventory() != null && item.getInventory().getProduct() != null) ? item.getInventory().getProduct().getId() : null;
                log.debug("  - Item ID: {}, Inventory ID: {}, Product ID: {}, Branch ID: {}, Quantity: {}", itemId, invId, prodId, branchId, itemQty);
            });
        } else {
            log.debug("Saved cart has null cartItems collection.");
        }
    }

    // --- Helper methods to map Entities to DTOs ---

    // Update mapToShoppingCartDTO to include branchId and potentially branch name
    private ShoppingCartDTO mapToShoppingCartDTO(ShoppingCart cart) {
        List<ShoppingCartItemDTO> itemDTOs = new java.util.ArrayList<>();
        if (cart != null && cart.getCartItems() != null) {
            itemDTOs = cart.getCartItems().stream()
                    .map(this::mapToShoppingCartItemDTO) // This method needs refinement
                    .collect(Collectors.toList());
        }

        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getUserId() : null);
        // Include branch details in the cart DTO
        if (cart.getBranch() != null) {
            dto.setBranchId(cart.getBranch().getBranchId());
            dto.setBranchName(cart.getBranch().getName()); // Add branch name if needed
        }
        dto.setItems(itemDTOs);
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        // Calculate total price (optional, can be done on frontend too)
        // double totalPrice = itemDTOs.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        // dto.setTotalPrice(totalPrice);

        return dto;
    }

    // Refine mapToShoppingCartItemDTO to include product details
    private ShoppingCartItemDTO mapToShoppingCartItemDTO(ShoppingCartItem item) {
        ShoppingCartItemDTO dto = new ShoppingCartItemDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setQuantity(item.getQuantity());
        dto.setAddedAt(item.getAddedAt());

        if (item.getInventory() != null) {
            BranchInventory inventory = item.getInventory();
            dto.setInventoryId(inventory.getInventoryId());
            // Assuming price is stored in BranchInventory, adjust if it's on Product
            dto.setPrice(inventory.getPrice()); // Or inventory.getSellingPrice(), etc.

            if (inventory.getProduct() != null) {
                Product product = inventory.getProduct();
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setProductImageUrl(product.getImageUrl()); // Add image URL
                // Add any other product details needed for display (e.g., unit)
                dto.setUnit(product.getUnit());
            }
        }
        return dto;
    }

    private ProductCartItemDTO mapToProductCartItemDTO(Product product, BranchInventory inventory) {
        // Assuming Product and BranchInventory are not null based on the caller's check
        return ProductCartItemDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .price(inventory.getPrice()) // Or getDiscountPrice() based on your logic
                .unit(product.getUnit()) // Assuming Product has a unit field
                .build();
    }

    @Override
    @Transactional
    public ShoppingCartDTO updateItemQuantity(Integer userId, Integer cartItemId, int newQuantity) {
        log.info("Attempting to update cart item ID: {} for user ID: {} to quantity: {}", cartItemId, userId, newQuantity);

        if (newQuantity <= 0) {
            log.warn("Invalid quantity provided for update: {}", newQuantity);
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        // 1. Find the cart item and verify ownership
        ShoppingCartItem cartItem = cartItemRepository.findByCartItemIdAndCart_User_UserId(cartItemId, userId)
                .orElseThrow(() -> {
                    log.warn("Cart item ID: {} not found or does not belong to user ID: {}", cartItemId, userId);
                    return new EntityNotFoundException("Cart item not found or access denied.");
                });

        log.debug("Found cart item ID: {} belonging to user ID: {}", cartItemId, userId);
        BranchInventory inventoryItem = cartItem.getInventory();
        if (inventoryItem == null) {
            // Should not happen with proper data integrity, but good to check
            log.error("Cart item ID: {} is missing inventory link.", cartItemId);
            throw new IllegalStateException("Cart item data is inconsistent.");
        }

        // 2. Check stock for the new quantity
        log.debug("Checking stock for inventory ID: {}. Available: {}, Requested: {}", inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), newQuantity);
        if (inventoryItem.getQuantityOnHand() < newQuantity) {
            log.warn("Insufficient stock for inventory ID: {} for requested quantity: {}. Available: {}", inventoryItem.getInventoryId(), newQuantity, inventoryItem.getQuantityOnHand());
            throw new InsufficientStockException("Insufficient stock for product ID " + inventoryItem.getProduct().getId() +
                    ". Requested: " + newQuantity + ", Available: " + inventoryItem.getQuantityOnHand());
        }

        // 3. Update quantity and timestamp
        cartItem.setQuantity(newQuantity);
        cartItem.setAddedAt(LocalDateTime.now()); // Or add an 'updatedAt' field to ShoppingCartItem
        // Also update the parent cart's timestamp
        ShoppingCart cart = cartItem.getCart();
        cart.setUpdatedAt(LocalDateTime.now());

        // 4. Save changes (Cart update cascades from item save if configured, or save cart explicitly)
        // Saving the item might be enough if cascade is set correctly. Saving the cart ensures its timestamp is updated.
        cartItemRepository.save(cartItem);
        ShoppingCart savedCart = cartRepository.save(cart); // Ensure cart timestamp is saved

        log.info("Successfully updated quantity for cart item ID: {} to {}", cartItemId, newQuantity);

        // 5. Return updated cart DTO
        return mapToShoppingCartDTO(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO removeItemFromCart(Integer userId, Integer cartItemId) {
        log.info("Attempting to remove cart item ID: {} for user ID: {}", cartItemId, userId);

        // 1. Find the cart item and verify ownership
        ShoppingCartItem cartItem = cartItemRepository.findByCartItemIdAndCart_User_UserId(cartItemId, userId)
                .orElseThrow(() -> {
                    log.warn("Cart item ID: {} not found or does not belong to user ID: {}", cartItemId, userId);
                    return new EntityNotFoundException("Cart item not found or access denied.");
                });

        log.debug("Found cart item ID: {} belonging to user ID: {}. Proceeding with removal.", cartItemId, userId);
        ShoppingCart cart = cartItem.getCart();

        // Remove the item from the cart's collection to avoid ObjectDeletedException
        if (cart.getCartItems() != null) {
            cart.getCartItems().remove(cartItem);
        }

        // Now delete the item from the repository
        cartItemRepository.delete(cartItem);
        log.debug("Deleted cart item ID: {}", cartItemId);

        // 3. Refresh the cart entity from the database to get the updated state
        Optional<ShoppingCart> refreshedCartOpt = cartRepository.findById(cart.getCartId());
    
        if (refreshedCartOpt.isPresent()) {
            ShoppingCart refreshedCart = refreshedCartOpt.get();
            // 4. Check if the cart is now empty
            if (refreshedCart.getCartItems() == null || refreshedCart.getCartItems().isEmpty()) {
                log.info("Cart ID: {} is now empty after removing item ID: {}. Deleting cart.", refreshedCart.getCartId(), cartItemId);
                cartRepository.delete(refreshedCart);
                return null;
            } else {
                refreshedCart.setUpdatedAt(LocalDateTime.now());
                ShoppingCart savedCart = cartRepository.save(refreshedCart);
                return mapToShoppingCartDTO(savedCart);
            }
        } else {
            // This case should ideally not happen if we start with a valid cartItem,
            // but handles potential race conditions or unexpected states.
            log.warn("Cart ID: {} could not be found after removing item ID: {}. It might have been deleted concurrently.", cart.getCartId(), cartItemId);
            return null; // Cart is gone
        }
    }
}