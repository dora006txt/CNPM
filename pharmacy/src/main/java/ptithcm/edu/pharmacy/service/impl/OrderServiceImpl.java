package ptithcm.edu.pharmacy.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
import ptithcm.edu.pharmacy.dto.OrderItemResponseDTO;
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.repository.OrderStatusRepository; // Import OrderStatusRepository
import ptithcm.edu.pharmacy.entity.OrderStatus; // Import OrderStatus entity/enum
import java.util.Set; 
import ptithcm.edu.pharmacy.service.OrderService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;
import ptithcm.edu.pharmacy.service.exception.ShoppingCartNotFoundException; // <-- Add this import
import ptithcm.edu.pharmacy.repository.PromotionRepository; // Add this import
import ptithcm.edu.pharmacy.entity.Promotion; // Add this import
import ptithcm.edu.pharmacy.entity.DiscountType; // Add this import
import java.math.RoundingMode; // Add this import

import jakarta.persistence.EntityNotFoundException; // Ensure correct import
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List; // Import List
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException; // For authorization check
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

@Service
@RequiredArgsConstructor 
public class OrderServiceImpl implements OrderService {

    // Define the cancellable statuses
    private static final Set<String> CANCELLABLE_STATUSES = Set.of("PENDING"); // Add other cancellable status names here if needed (e.g., "PROCESSING")

    private final OrderRepository orderRepository; // Injected here
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository; // Need this to delete items
    private final PromotionRepository promotionRepository; // Add this line
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class); // Add logger instance
    // private final DiscountRepository discountRepository; // In a real app, inject this


    @Override
    @Transactional
    public OrderResponseDTO createOrderFromCart(CreateOrderRequestDTO createOrderRequestDTO, Integer userId) {
        // 1. Fetch User and related entities for the order
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Branch branch = branchRepository.findById(createOrderRequestDTO.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + createOrderRequestDTO.getBranchId()));

        ShippingMethod shippingMethod = shippingMethodRepository.findById(createOrderRequestDTO.getShippingMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping Method not found with ID: " + createOrderRequestDTO.getShippingMethodId()));

        PaymentType paymentType = paymentTypeRepository.findById(createOrderRequestDTO.getPaymentTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Payment Type not found with ID: " + createOrderRequestDTO.getPaymentTypeId()));

        OrderStatus initialStatus = orderStatusRepository.findByStatusNameIgnoreCase("PENDING")
                .orElseThrow(() -> new EntityNotFoundException("Default order status 'PENDING' not found."));

        // 2. Fetch User's ShoppingCart for the specific branch
        ShoppingCart cart = shoppingCartRepository.findByUser_UserIdAndBranch_BranchId(userId, createOrderRequestDTO.getBranchId()) // Use findByUser_UserIdAndBranch_BranchId
                .orElseThrow(() -> new ShoppingCartNotFoundException("Shopping cart not found for user ID: " + userId + " and branch ID: " + createOrderRequestDTO.getBranchId())); // No change needed here once import is added

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order from an empty shopping cart.");
        }

        // 3. Create Order entity
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setUser(user);
        order.setBranch(branch); // Order placed against this branch
        order.setOrderDate(LocalDateTime.now());
        order.setShippingMethod(shippingMethod);
        order.setOrderStatus(initialStatus);
        order.setPaymentType(paymentType);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setNotes(createOrderRequestDTO.getNotes());
        order.setRequiresConsultation(createOrderRequestDTO.getRequiresConsultation());
        order.setConsultationStatus(createOrderRequestDTO.getRequiresConsultation() ? ConsultationStatus.PENDING : ConsultationStatus.NOT_REQUIRED);
        // Timestamps handled by annotations

        // Initialize totals and items set
        BigDecimal subtotal = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        // 4. Loop through ShoppingCartItems
        // Create a copy of the items to avoid ConcurrentModificationException if clearing cart later by item
        Set<ShoppingCartItem> itemsToProcess = new HashSet<>(cart.getCartItems());

        for (ShoppingCartItem cartItem : itemsToProcess) {
            // Access Product through BranchInventory
            BranchInventory inventory = cartItem.getInventory();
            if (inventory == null) {
                 // Handle cases where inventory might be missing (data integrity issue)
                throw new IllegalStateException("Cart item ID " + cartItem.getCartItemId() + " is missing inventory information.");
            }
            Product product = inventory.getProduct();
            if (product == null) {
                // Handle cases where product might be missing from inventory (data integrity issue)
                throw new IllegalStateException("Inventory ID " + inventory.getInventoryId() + " is missing product information.");
            }
            int quantity = cartItem.getQuantity();

            // Fetch inventory specific to the order's branch and product (redundant check, already have inventory from cartItem)
            // BranchInventory inventory = branchInventoryRepository.findByBranch_BranchIdAndProduct_Id(branch.getBranchId(), product.getId())
            //         .orElseThrow(() -> new EntityNotFoundException(
            //                 "Inventory not found for Product ID " + product.getId() + " at Branch ID " + branch.getBranchId()));

            // Check stock using the inventory from the cart item
            if (inventory.getQuantityOnHand() < quantity) {
                throw new InsufficientStockException(
                        "Insufficient stock for Product ID " + product.getId() + " (" + product.getName() + ") at Branch ID " + inventory.getBranch().getBranchId() + // Use inventory's branch
                        ". Requested: " + quantity + ", Available: " + inventory.getQuantityOnHand());
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setInventory(inventory); // Use the inventory from the cart item
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(inventory.getPrice()); // Use current price
            BigDecimal itemSubtotal = inventory.getPrice().multiply(BigDecimal.valueOf(quantity));
            orderItem.setSubtotal(itemSubtotal);

            orderItems.add(orderItem);
            subtotal = subtotal.add(itemSubtotal);

            // Decrease stock
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
            branchInventoryRepository.save(inventory);
        }

        // 5. Set items and calculated totals on the Order
        order.setOrderItems(orderItems);
        order.setSubtotalAmount(subtotal);
        BigDecimal shippingFee = shippingMethod.getBaseCost() != null ? shippingMethod.getBaseCost() : BigDecimal.ZERO;
        order.setShippingFee(shippingFee);
        
        // --- Apply Promotion Code Logic ---
        BigDecimal calculatedDiscount = BigDecimal.ZERO;
        String promotionCode = createOrderRequestDTO.getPromotionCode();
        String effectiveAppliedPromotionCode = null;
        Promotion appliedPromotionEntity = null; // To store the promotion entity if applied

        if (promotionCode != null && !promotionCode.isBlank()) {
            log.info("Attempting to apply promotion code: {}", promotionCode);
            Optional<Promotion> promotionOpt = promotionRepository.findByCode(promotionCode);

            if (promotionOpt.isPresent()) {
                Promotion promotion = promotionOpt.get();
                LocalDateTime now = LocalDateTime.now();
                boolean isValid = true;

                if (!promotion.getIsActive()) {
                    log.warn("Promotion code {} is not active.", promotionCode);
                    isValid = false;
                }
                if (isValid && promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
                    log.warn("Promotion code {} is not yet valid. Starts at {}", promotionCode, promotion.getStartDate());
                    isValid = false;
                }
                if (isValid && promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
                    log.warn("Promotion code {} has expired. Ended at {}", promotionCode, promotion.getEndDate());
                    isValid = false;
                }
                if (isValid && promotion.getMinOrderValue() != null && subtotal.compareTo(promotion.getMinOrderValue()) < 0) {
                    log.warn("Promotion code {} requires a minimum subtotal of {}. Current subtotal: {}", promotionCode, promotion.getMinOrderValue(), subtotal);
                    isValid = false;
                }
                if (isValid && promotion.getTotalUsageLimit() != null && promotion.getTotalUsedCount() >= promotion.getTotalUsageLimit()) {
                    log.warn("Promotion code {} has reached its total usage limit.", promotionCode);
                    isValid = false;
                }
                // Note: Per-customer usage limits and specific applicability (category/product/branch) are not checked here for simplicity.

                if (isValid) {
                    if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                        calculatedDiscount = subtotal.multiply(promotion.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                        if (calculatedDiscount.compareTo(subtotal) > 0) { // Cap discount at subtotal
                            calculatedDiscount = subtotal;
                        }
                        log.info("Applied PERCENTAGE discount of {} for code {}. Original subtotal: {}, Discount value: {}%", calculatedDiscount, promotionCode, subtotal, promotion.getDiscountValue());
                    } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                        calculatedDiscount = promotion.getDiscountValue();
                        if (calculatedDiscount.compareTo(subtotal) > 0) { // Cap discount at subtotal
                            calculatedDiscount = subtotal;
                        }
                        log.info("Applied FIXED_AMOUNT discount of {} for code {}. Original subtotal: {}, Discount value: {}", calculatedDiscount, promotionCode, subtotal, promotion.getDiscountValue());
                    } else {
                        log.warn("Promotion code {} has an unsupported discount type: {}", promotionCode, promotion.getDiscountType());
                    }

                    if (calculatedDiscount.compareTo(BigDecimal.ZERO) > 0) {
                        effectiveAppliedPromotionCode = promotion.getCode(); // Store the code that was successfully applied
                        appliedPromotionEntity = promotion; // Store the entity
                        log.info("Promotion {} successfully applied with discount amount: {}", effectiveAppliedPromotionCode, calculatedDiscount);
                    } else {
                        log.warn("Calculated discount for promotion code {} is zero or less. Not applying.", promotionCode);
                        // No need to set effectiveAppliedPromotionCode or appliedPromotionEntity if discount is zero
                    }
                } else {
                    log.warn("Promotion code {} is not valid or not applicable.", promotionCode);
                }
            } else {
                log.warn("Promotion code {} not found in the database.", promotionCode);
            }
        }

        order.setDiscountAmount(calculatedDiscount);
        order.setAppliedPromotionCode(effectiveAppliedPromotionCode);

        // 5c. Calculate Final Amount
        BigDecimal finalAmount = subtotal.add(shippingFee).subtract(calculatedDiscount);
        order.setFinalAmount(finalAmount);

        // Set shipping address - UNCOMMENT THIS LOGIC
        if (createOrderRequestDTO.getShippingAddress() != null && !createOrderRequestDTO.getShippingAddress().isBlank()) {
            // Assuming Order entity has a shippingAddress field (String)
            order.setShippingAddress(createOrderRequestDTO.getShippingAddress()); // Use address from request DTO if provided
        } else if (user.getAddress() != null) {
             // Assuming Order entity has a shippingAddress field and User has getAddress() returning String
             order.setShippingAddress(user.getAddress()); // Fallback to user's default address
        }
        // If neither is available, shippingAddress on the order will remain null

        // 6. Save Order (cascades to OrderItems)
        Order savedOrder = orderRepository.save(order);

        // 7. Clear the user's ShoppingCart
        // Option 1: Delete all items directly (requires ShoppingCartItemRepository)
        shoppingCartItemRepository.deleteAll(itemsToProcess); // Delete the processed items

        // After deleting items, delete the cart itself
        shoppingCartRepository.delete(cart); // Add this line to delete the shopping cart

        // Option 2: Clear the collection and update the cart (if cascade remove is set on ShoppingCart entity)
        // cart.getCartItems().clear();
        // shoppingCartRepository.save(cart); // This would only clear items, not delete the cart if not configured for orphan removal

        // 8. Map saved Order to OrderResponseDTO
        // Fetch again to be safe, especially if OrderItems fetch type is LAZY
        Order fetchedOrder = orderRepository.findById(savedOrder.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Failed to fetch the created order with ID: " + savedOrder.getOrderId()));

        // 9. Return DTO
        // Use the fetchedOrder directly
        return mapOrderToResponseDTO(fetchedOrder); // This will now use the address set on the saved order
    }

    
    @Override
@Transactional(readOnly = true)
public List<OrderResponseDTO> findOrdersByUserId(Integer userId) {
    List<Order> orders = orderRepository.findByUser_UserId(userId);
    log.info("Found {} orders for user ID: {}", orders.size(), userId); // Log count
    try {
        return orders.stream()
                .map(order -> {
                    log.info("Mapping order ID: {}", order.getOrderId()); // Log each order being mapped
                    return mapOrderToResponseDTO(order);
                })
                .collect(Collectors.toList());
    } catch (Exception e) {
        log.error("Error during order mapping for user ID: {}", userId, e); // Log any exception during mapping
        throw e; // Re-throw the exception
    }
}
    // --- End of findOrdersByUserId implementation ---

    // --- Add Implementation for findOrderById ---
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findOrderById(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("User " + userId + " is not authorized to view order " + orderId);
        }
        // --- End Authorization Check ---

        // Use the existing mapping method
        return mapOrderToResponseDTO(order);
    }
    // --- End of findOrderById implementation ---


    // --- Implementation for cancelOrder ---
    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Integer orderId, Integer userId) {
        log.info("Attempting cancellation for order ID: {} by user ID: {}", orderId, userId);

        // 1. Fetch the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Cancellation failed: Order not found with ID: {}", orderId);
                    return new EntityNotFoundException("Order not found with id: " + orderId);
                });

        // 2. Authorization Check: Verify the user owns the order
        if (order.getUser() == null || !order.getUser().getUserId().equals(userId)) {
            String ownerId = (order.getUser() != null) ? String.valueOf(order.getUser().getUserId()) : "unknown";
            log.warn("Authorization failed: User ID {} attempted to cancel order ID {} owned by User ID {}",
                    userId, orderId, ownerId);
            throw new AccessDeniedException("User is not authorized to cancel this order.");
        }
        log.debug("User {} authorized to cancel order {}", userId, orderId);

        // 3. Status Check: Verify if the order is in a cancellable state
        String currentStatusName = order.getOrderStatus().getStatusName();
        if (!CANCELLABLE_STATUSES.contains(currentStatusName.toUpperCase())) { // Use toUpperCase for robust comparison
            log.warn("Cancellation failed: Order ID {} has non-cancellable status: {}", orderId, currentStatusName);
            throw new IllegalStateException(
                    "Order cannot be cancelled because its current status is '" + currentStatusName + "'.");
        }
        log.debug("Order ID {} has cancellable status: {}", orderId, currentStatusName);

        // 4. Fetch the 'CANCELLED' status entity
        OrderStatus cancelledStatus = orderStatusRepository.findByStatusNameIgnoreCase("FAILED")
                .orElseThrow(() -> {
                    log.error("Critical error: 'CANCELLED' order status definition not found in the database.");
                    return new EntityNotFoundException("System configuration error: 'CANCELLED' status not found.");
                });

        // 5. Restore Inventory Stock
        log.debug("Restoring inventory for order ID: {}", orderId);
        for (OrderItem item : order.getOrderItems()) {
            BranchInventory inventory = item.getInventory();
            // Check if inventory exists for the item (robustness)
            if (inventory != null) {
                int quantityToRestore = item.getQuantity();
                inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantityToRestore);
                branchInventoryRepository.save(inventory); // Save updated inventory
                log.debug("Restored {} units for inventory ID: {} (Product ID: {})",
                        quantityToRestore, inventory.getInventoryId(), item.getProduct().getId());
            } else {
                // Log a warning if an order item lacks inventory link - potential data issue
                log.warn("Inventory link missing for OrderItem ID: {} in Order ID: {}. Stock not restored for this item.",
                        item.getOrderItemId(), orderId);
            }
        }

        // 6. Update Order Status and Timestamps
        order.setOrderStatus(cancelledStatus);
        order.setCancelledAt(LocalDateTime.now()); // Record cancellation time

        // 7. Update Payment Status (if applicable)
        // If payment was pending, mark it as failed/cancelled to prevent further processing.
        if (order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.FAILED); // Or a dedicated CANCELLED status if you add one to PaymentStatus enum
            log.debug("Set payment status to FAILED for order ID: {}", orderId);
        }

        // 8. Save the updated order
        Order savedOrder = orderRepository.save(order);
        log.info("Successfully cancelled order ID: {}", savedOrder.getOrderId());

        // 9. Map to DTO and return
        return mapOrderToResponseDTO(savedOrder); // Use the existing mapping function
    }
    // --- End of cancelOrder implementation ---

    // --- Helper Methods (mapOrderToResponseDTO, mapOrderItemToResponseDTO, generateOrderCode) ---

    // --- Helper method to map Order to OrderResponseDTO ---
    private OrderResponseDTO mapOrderToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderCode(order.getOrderCode());

        // Map User info (handle potential null)
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getUserId());
            dto.setUserFullName(order.getUser().getFullName()); // Assuming User entity has getFullName()
        }

        // Map Branch info (handle potential null)
        if (order.getBranch() != null) {
            dto.setBranchId(order.getBranch().getBranchId());
            dto.setBranchName(order.getBranch().getName()); // Assuming Branch entity has getName()
        }

        dto.setOrderDate(order.getOrderDate());

        // Map Shipping Method info (handle potential null)
        if (order.getShippingMethod() != null) {
            dto.setShippingMethodId(order.getShippingMethod().getMethodId());
            dto.setShippingMethodName(order.getShippingMethod().getName()); // Assuming ShippingMethod has getName()
        }

        // Map Order Status info (handle potential null)
        if (order.getOrderStatus() != null) {
            dto.setOrderStatusId(order.getOrderStatus().getStatusId());
            dto.setOrderStatusName(order.getOrderStatus().getStatusName()); // Assuming OrderStatus has getStatusName()
        }

        dto.setSubtotalAmount(order.getSubtotalAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setAppliedPromotionCode(order.getAppliedPromotionCode()); // Map the applied promotion code
        dto.setFinalAmount(order.getFinalAmount());

        // Map Payment Type info (handle potential null)
        if (order.getPaymentType() != null) {
            dto.setPaymentTypeId(order.getPaymentType().getPaymentTypeId());
            dto.setPaymentTypeName(order.getPaymentType().getTypeName()); // Assuming PaymentType has getTypeName()
        }

        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNotes(order.getNotes());
        dto.setRequiresConsultation(order.getRequiresConsultation());

        // Map Assigned Staff info (handle potential null)
        if (order.getAssignedStaff() != null) {
            dto.setAssignedStaffId(order.getAssignedStaff().getStaffId());
            dto.setAssignedStaffName(order.getAssignedStaff().getFullName()); // Assuming Staff has getFullName()
        }

        dto.setConsultationStatus(order.getConsultationStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setCancelledAt(order.getCancelledAt()); // Map the cancelledAt field

        // Map OrderItems (handle potential null or empty list)
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::mapOrderItemToResponseDTO) // Use the existing helper method
                    .collect(Collectors.toList()));
        } else {
            dto.setOrderItems(Collections.emptyList()); // Set to empty list if null or empty
        }

        dto.setShippingAddress(order.getShippingAddress()); // Map shipping address

        // Determine if the order is cancellable based on its current status
        String currentStatus = (order.getOrderStatus() != null) ? order.getOrderStatus().getStatusName().toUpperCase() : "";
        dto.setIsCancellable(CANCELLABLE_STATUSES.contains(currentStatus));

        return dto;
    }

    // Ensure mapOrderItemToResponseDTO exists
    private OrderItemResponseDTO mapOrderItemToResponseDTO(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setOrderItemId(item.getOrderItemId());

        Product product = item.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            // Assuming OrderItemResponseDTO has setImageUrl and Product has getImageUrl
            try {
                 // Direct method call is preferred
                dto.setProductImageUrl(product.getImageUrl());
            } catch (Exception e) {
                 // Log if the setter doesn't exist or fails
                log.trace("Could not set image URL for product ID {} on OrderItemResponseDTO", product.getId(), e);
                 dto.setProductImageUrl(null); // Ensure it's null if setting fails
            }
        } else {
            log.warn("Product is null for OrderItem ID: {}", item.getOrderItemId());
            dto.setProductId(null);
            dto.setProductName("Product information missing");
            try {
                Method setImageUrlMethod = OrderItemResponseDTO.class.getMethod("setProductImageUrl", String.class);
                 setImageUrlMethod.invoke(dto, (String) null); // Set image URL to null
            } catch (NoSuchMethodException e) {
                 // Already logged above, do nothing
            } catch (Exception e) {
                log.error("Error setting image URL to null via reflection", e);
            }
        }

        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setSubtotal(item.getSubtotal());

        return dto;
    }

    private String generateOrderCode() {
        // Simple implementation using UUID (first 8 chars)
        // You can customize this logic (e.g., timestamp + sequence)
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}