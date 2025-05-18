package ptithcm.edu.pharmacy.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.*;
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.service.ShoppingCartService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections; // Add this import
import java.util.HashSet; // Thêm import này
import java.util.stream.Collectors; // Add this import

import ptithcm.edu.pharmacy.service.exception.ShoppingCartNotFoundException; // Đảm bảo import này tồn tại

@Service
@RequiredArgsConstructor
@Slf4j // Add Lombok logging annotation
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final UserRepository userRepository;
    private final BranchInventoryRepository inventoryRepository; // Đảm bảo đã inject

    @Override
    @Transactional
    public ShoppingCartDTO addItemToCart(Integer userId, AddToCartRequest request) {
        log.info("addItemToCart started for userId: {}, request: (InventoryId: {}, Quantity: {})", userId,
                request.getInventoryId(), request.getQuantity());

        // 1. Find the User
        log.debug("Finding user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });
        log.debug("User found: {}", user.getFullName());

        // 2. Find the specific BranchInventory item using inventoryId from the request
        log.debug("Finding inventory item with ID: {}", request.getInventoryId());
        BranchInventory inventoryItem = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> {
                    log.warn("Inventory item not found with ID: {}", request.getInventoryId());
                    return new EntityNotFoundException("Inventory item not found with ID: " + request.getInventoryId());
                });

        // Lấy productId và branchId từ inventoryItem
        Integer productId = inventoryItem.getProduct().getId();
        Integer branchId = inventoryItem.getBranch().getBranchId();
        log.debug("Inventory item found: ID={}, ProductID={}, BranchID={}", inventoryItem.getInventoryId(), productId,
                branchId);

        // 3. Check stock for the requested quantity initially
        log.debug("Checking initial stock for inventoryId: {}. Available: {}, Requested: {}",
                inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), request.getQuantity());
        if (inventoryItem.getQuantityOnHand() < request.getQuantity()) {
            log.warn("Insufficient stock for inventoryId: {}. Available: {}, Requested: {}",
                    inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), request.getQuantity());
            throw new InsufficientStockException("Insufficient stock for product ID " + productId +
                    " at branch ID " + branchId + ". Available: " + inventoryItem.getQuantityOnHand());
        }

        // 4. Find the user's SINGLE ShoppingCart
        log.debug("Finding cart for userId: {}", userId);
        // Optional<ShoppingCart> cartOptional =
        // cartRepository.findByUser_UserIdAndBranch_BranchId(userId,
        // itemBranch.getBranchId()); // DÒNG CŨ
        Optional<ShoppingCart> cartOptional = cartRepository.findByUser_UserId(userId); // DÒNG MỚI: Tìm giỏ hàng duy
                                                                                        // nhất của người dùng

        ShoppingCart cart;
        boolean isNewCart = false;

        if (cartOptional.isPresent()) {
            // 4a. Cart exists, use it
            cart = cartOptional.get();
            log.debug("Existing cart found for user {}. Cart ID: {}", userId, cart.getCartId());
        } else {
            // 4b. No cart exists, create a new one
            log.info("No existing cart found for userId: {}. Creating new cart.", userId);
            cart = createNewCart(user); // DÒNG MỚI: Không cần truyền branch nữa
            isNewCart = true;
            log.debug("Created new cart object (not yet saved).");
        }

        // 5. Check if the item (inventoryId) is already in the selected cart
        log.debug("Checking if inventory item ID: {} exists in cart ID: {}", inventoryItem.getInventoryId(),
                cart.getCartId());
        final Integer inventoryIdToCheck = inventoryItem.getInventoryId();
        Optional<ShoppingCartItem> existingCartItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getInventory() != null
                        && item.getInventory().getInventoryId().equals(inventoryIdToCheck))
                .findFirst();

        if (existingCartItemOpt.isPresent()) {
            // 5a. Item exists in cart, update quantity
            ShoppingCartItem existingCartItem = existingCartItemOpt.get();
            log.debug("Inventory item ID: {} found in cart (CartItemID: {}). Updating quantity.",
                    inventoryItem.getInventoryId(),
                    existingCartItem.getCartItemId());
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            log.debug("New quantity calculation: {} (current) + {} (request) = {}", existingCartItem.getQuantity(),
                    request.getQuantity(), newQuantity);

            // 5b. Re-check stock for the TOTAL quantity needed
            if (inventoryItem.getQuantityOnHand() < newQuantity) {
                log.warn(
                        "Insufficient stock for inventoryId: {} for updated quantity. Available: {}, Requested total: {}",
                        inventoryItem.getInventoryId(), inventoryItem.getQuantityOnHand(), newQuantity);
                throw new InsufficientStockException("Insufficient stock for product ID " + productId +
                        " at branch ID " + branchId + ". Requested total: " + newQuantity +
                        ", Available: " + inventoryItem.getQuantityOnHand());
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setAddedAt(LocalDateTime.now());
            log.debug("Updated quantity for CartItemID: {} to {}", existingCartItem.getCartItemId(), newQuantity);
        } else {
            // 5c. Item does not exist in cart, add new item
            log.debug("Inventory item ID: {} not found in cart. Creating new ShoppingCartItem.", inventoryIdToCheck);
            ShoppingCartItem newItem = new ShoppingCartItem();
            newItem.setCart(cart);
            newItem.setInventory(inventoryItem);
            newItem.setQuantity(request.getQuantity());
            newItem.setAddedAt(LocalDateTime.now());
            cart.getCartItems().add(newItem);
            log.debug("Added new ShoppingCartItem to cart's item collection.");
        }

        // 6. Update cart timestamp and save
        cart.setUpdatedAt(LocalDateTime.now());

        log.debug(">>> Preparing to save cart <<<");
        log.debug("Cart ID (before save): {}", cart.getCartId());
        log.debug("Cart User ID: {}", (cart.getUser() != null) ? cart.getUser().getUserId() : "null");
        // log.debug("Cart Branch ID: {}", (cart.getBranch() != null) ?
        // cart.getBranch().getBranchId() : "null"); // Không còn branch ở cart level
        log.debug("Is New Cart flag: {}", isNewCart);
        log.debug("Number of items in cart collection: {}",
                cart.getCartItems() != null ? cart.getCartItems().size() : "null collection");
        if (cart.getCartItems() != null) {
            cart.getCartItems().forEach(item -> {
                log.debug("  - Item (hashCode: {}): Quantity={}, InventoryID={}, AddedAt={}",
                        item.hashCode(),
                        item.getQuantity(),
                        (item.getInventory() != null) ? item.getInventory().getInventoryId() : "null",
                        item.getAddedAt());
                log.debug("    Item's cart reference matches parent? {}", item.getCart() == cart);
            });
        }
        log.debug(">>> Proceeding with cartRepository.save(cart) <<<");

        ShoppingCart savedCart = cartRepository.save(cart);
        log.info("Successfully saved cart ID: {}", savedCart.getCartId());

        logSavedCartState(savedCart); // Gọi phương thức log

        // 7. Map to DTO and return
        log.debug("Mapping saved cart to DTO.");
        return mapToShoppingCartDTO(savedCart);
    }

    // Sửa đổi createNewCart để không yêu cầu Branch
    private ShoppingCart createNewCart(User user) { // Bỏ Branch parameter
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        // cart.setBranch(branch); // Bỏ dòng này
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setCartItems(new HashSet<>()); // Khởi tạo set rỗng
        log.info("Creating new cart for user ID: {}", user.getUserId());
        return cart;
    }

    // Phương thức getCartsByUserId đã bị loại bỏ vì không còn trong interface và
    // controller đã dùng getCartByUserId
    // public List<ShoppingCartDTO> getCartsByUserId(Integer userId) { ... }

    // Phương thức mới (hoặc thay thế getCartsByUserId) để lấy giỏ hàng duy nhất
    @Override
    @Transactional(readOnly = true)
    public Optional<ShoppingCartDTO> getCartByUserId(Integer userId) {
        log.info("Fetching single cart for userId: {}", userId);
        return cartRepository.findByUser_UserId(userId)
                .map(this::mapToShoppingCartDTO);
    }

    @Override
    @Transactional
    public ShoppingCartDTO updateItemQuantity(Integer userId, Integer cartItemId, int quantity) {
        log.info("updateItemQuantity called for userId: {}, cartItemId: {}, quantity: {}", userId, cartItemId,
                quantity);
        if (quantity <= 0) {
            log.warn("Quantity must be positive. Received: {}", quantity);
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        // Tìm giỏ hàng duy nhất của người dùng
        ShoppingCart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    log.warn("Shopping cart not found for user ID: {}", userId);
                    return new ShoppingCartNotFoundException("Shopping cart not found for user ID: " + userId);
                });
        log.debug("Cart found for user ID: {}. Cart ID: {}", userId, cart.getCartId());

        ShoppingCartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Cart item with ID {} not found in cart ID {}", cartItemId, cart.getCartId());
                    return new EntityNotFoundException("Item with ID " + cartItemId + " not found in your cart.");
                });
        log.debug("Item found in cart: CartItemID: {}", itemToUpdate.getCartItemId());

        BranchInventory inventory = itemToUpdate.getInventory();
        if (inventory.getQuantityOnHand() < quantity) {
            log.warn("Insufficient stock for inventoryId: {}. Available: {}, Requested: {}",
                    inventory.getInventoryId(), inventory.getQuantityOnHand(), quantity);
            throw new InsufficientStockException("Insufficient stock for product ID " + inventory.getProduct().getId() +
                    ". Available: " + inventory.getQuantityOnHand());
        }

        itemToUpdate.setQuantity(quantity);
        itemToUpdate.setAddedAt(LocalDateTime.now()); // Cập nhật thời gian
        cart.setUpdatedAt(LocalDateTime.now());

        // Không cần cartItemRepository.save(itemToUpdate) nếu cascade được cấu hình
        // đúng
        ShoppingCart savedCart = cartRepository.save(cart); // Lưu lại giỏ hàng sẽ cascade đến item
        log.info("Successfully updated quantity for item ID {} in cart ID {}", cartItemId, savedCart.getCartId());
        return mapToShoppingCartDTO(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO removeItemFromCart(Integer userId, Integer cartItemId) {
        log.info("removeItemFromCart called for userId: {}, cartItemId: {}", userId, cartItemId);

        // Tìm giỏ hàng duy nhất của người dùng
        ShoppingCart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    log.warn("Shopping cart not found for user ID: {}", userId);
                    return new ShoppingCartNotFoundException("Shopping cart not found for user ID: " + userId);
                });
        log.debug("Cart found for user ID: {}. Cart ID: {}", userId, cart.getCartId());

        ShoppingCartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Cart item with ID {} not found in cart ID {}", cartItemId, cart.getCartId());
                    return new EntityNotFoundException("Item with ID " + cartItemId + " not found in your cart.");
                });
        log.debug("Item found for removal: CartItemID: {}", itemToRemove.getCartItemId());

        cart.getCartItems().remove(itemToRemove); // Xóa item khỏi collection
        itemToRemove.setCart(null); // Ngắt liên kết (quan trọng nếu orphanRemoval=true không đủ hoặc để an toàn)

        cart.setUpdatedAt(LocalDateTime.now());
        ShoppingCart savedCart = cartRepository.save(cart); // Lưu lại giỏ hàng

        log.info("Successfully removed item ID {} from cart ID {}", cartItemId, savedCart.getCartId());
        return mapToShoppingCartDTO(savedCart);
    }

    private ShoppingCartDTO mapToShoppingCartDTO(ShoppingCart cart) {
        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser().getUserId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());

        if (cart.getCartItems() != null) {
            dto.setItems(cart.getCartItems().stream()
                    .map(this::mapToShoppingCartItemDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(Collections.emptyList());
        }
        return dto;
    }

    private ShoppingCartItemDTO mapToShoppingCartItemDTO(ShoppingCartItem item) {
        ShoppingCartItemDTO dto = new ShoppingCartItemDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setQuantity(item.getQuantity());
        dto.setAddedAt(item.getAddedAt());

        if (item.getInventory() != null) {
            BranchInventory inventory = item.getInventory();
            dto.setInventoryId(inventory.getInventoryId());
            dto.setPrice(inventory.getPrice()); // Or inventory.getSellingPrice(), etc.

            // Thêm thông tin chi nhánh vào DTO của item
            if (inventory.getBranch() != null) {
                dto.setBranchId(inventory.getBranch().getBranchId()); // Giả sử ShoppingCartItemDTO có setBranchId
                dto.setBranchName(inventory.getBranch().getName()); // Giả sử ShoppingCartItemDTO có setBranchName
            }

            if (inventory.getProduct() != null) {
                Product product = inventory.getProduct();
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setProductImageUrl(product.getImageUrl());
                dto.setUnit(product.getUnit());
            }
        }
        return dto;
    }

    // Thêm phương thức logSavedCartState (stub implementation)
    private void logSavedCartState(ShoppingCart cart) {
        if (cart == null) {
            log.debug("logSavedCartState called with null cart.");
            return;
        }
        log.debug("State of saved cart ID {}: User={}, ItemsSize={}",
                cart.getCartId(),
                (cart.getUser() != null ? cart.getUser().getUserId() : "null"),
                (cart.getCartItems() != null ? cart.getCartItems().size() : 0));
        if (cart.getCartItems() != null) {
            cart.getCartItems().forEach(item -> {
                if (item == null) {
                    log.debug("  - Saved Item: null item in cart.");
                    return;
                }
                log.debug("  - Saved Item: CartItemID={}, InventoryID={}, Quantity={}, BranchID={}",
                        item.getCartItemId(),
                        (item.getInventory() != null ? item.getInventory().getInventoryId() : "null"),
                        item.getQuantity(),
                        (item.getInventory() != null && item.getInventory().getBranch() != null
                                ? item.getInventory().getBranch().getBranchId()
                                : "null"));
            });
        }
    }
}