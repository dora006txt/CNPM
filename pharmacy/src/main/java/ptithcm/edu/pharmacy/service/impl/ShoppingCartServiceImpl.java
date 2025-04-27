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
import ptithcm.edu.pharmacy.service.ShoppingCartService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;

import java.time.LocalDateTime;
import java.util.List; // Make sure List is imported
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Add Lombok logging annotation
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final BranchInventoryRepository inventoryRepository;
    // Assuming ShoppingCartItemRepository exists or is managed via cascade

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

    // Update mapToShoppingCartDTO to include branchId
    private ShoppingCartDTO mapToShoppingCartDTO(ShoppingCart cart) {
        List<ShoppingCartItemDTO> itemDTOs = new java.util.ArrayList<>();
        if (cart != null && cart.getCartItems() != null) {
            itemDTOs = cart.getCartItems().stream()
                    .map(this::mapToShoppingCartItemDTO)
                    .collect(Collectors.toList());
        }

        Integer userId = (cart != null && cart.getUser() != null) ? cart.getUser().getUserId() : null;
        Integer branchId = (cart != null && cart.getBranch() != null) ? cart.getBranch().getBranchId() : null; // Get branchId

        return ShoppingCartDTO.builder()
                .cartId(cart != null ? cart.getCartId() : null)
                .userId(userId)
                .branchId(branchId) // Add branchId to DTO
                .cartItems(itemDTOs)
                .createdAt(cart != null ? cart.getCreatedAt() : null)
                .updatedAt(cart != null ? cart.getUpdatedAt() : null)
                .build();
    }

    // mapToShoppingCartItemDTO and mapToProductCartItemDTO remain the same
    private ShoppingCartItemDTO mapToShoppingCartItemDTO(ShoppingCartItem item) {
        // Perform null checks for safety
        BranchInventory inventory = (item != null) ? item.getInventory() : null;
        Product product = (inventory != null) ? inventory.getProduct() : null;
        Integer inventoryId = (inventory != null) ? inventory.getInventoryId() : null;

        ProductCartItemDTO productDTO = null;
        if (product != null && inventory != null) {
             productDTO = mapToProductCartItemDTO(product, inventory);
        }

        return ShoppingCartItemDTO.builder()
                .cartItemId(item != null ? item.getCartItemId() : null)
                .inventoryId(inventoryId) // Use Integer ID from BranchInventory
                .product(productDTO)
                .quantity(item != null ? item.getQuantity() : null)
                .addedAt(item != null ? item.getAddedAt() : null)
                .build();
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
}