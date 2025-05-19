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
import ptithcm.edu.pharmacy.entity.PromotionUsage; // <-- Add this import
import ptithcm.edu.pharmacy.repository.PromotionUsageRepository; // <-- Add this import
import java.math.RoundingMode; // Add this import
import ptithcm.edu.pharmacy.entity.ApplicableScope; // <-- Add this import

import jakarta.persistence.EntityNotFoundException; // Ensure correct import
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List; // Import List
import java.util.Map;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException; // For authorization check
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    // Define the cancellable statuses
    private static final Set<String> CANCELLABLE_STATUSES = Set.of("PENDING");

    // Định nghĩa các trạng thái mà admin có thể chuyển đến và các chuyển đổi hợp lệ
    // Đảm bảo trường này được khai báo chính xác như sau:
    private static final Map<String, Set<String>> VALID_STATUS_TRANSITIONS_BY_ADMIN = Map.of(
            "PENDING", Set.of("CONFIRMED", "PROCESSING", "CANCELLED", "ON_HOLD"),
            "CONFIRMED", Set.of("PROCESSING", "SHIPPED", "CANCELLED"),
            "PROCESSING", Set.of("SHIPPED", "DELIVERED", "CANCELLED", "ON_HOLD"),
            "SHIPPED", Set.of("DELIVERED", "RETURNED"),
            "ON_HOLD", Set.of("PENDING", "PROCESSING", "CANCELLED")
    // Các trạng thái cuối như DELIVERED, CANCELLED, RETURNED thường không cho phép
    // chuyển tiếp nữa
    );

    private final OrderRepository orderRepository; // Injected here
    private final UserRepository userRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository; // Need this to delete items
    private final PromotionRepository promotionRepository; // Add this line
    private final PromotionUsageRepository promotionUsageRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrderFromCart(CreateOrderRequestDTO createOrderRequestDTO, Integer userId) {
        log.info("Creating consolidated order from cart for userId: {}", userId); // Removed branchId from log
        // 1. Fetch User and related entities for the order
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });

        ShippingMethod shippingMethod = shippingMethodRepository.findById(createOrderRequestDTO.getShippingMethodId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping Method not found with ID: " + createOrderRequestDTO.getShippingMethodId()));

        PaymentType paymentType = paymentTypeRepository.findById(createOrderRequestDTO.getPaymentTypeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment Type not found with ID: " + createOrderRequestDTO.getPaymentTypeId()));

        OrderStatus initialStatus = orderStatusRepository.findByStatusNameIgnoreCase("PENDING")
                .orElseThrow(() -> new EntityNotFoundException("Default order status 'PENDING' not found."));

        // 2. Fetch User's single ShoppingCart
        log.debug("Fetching shopping cart for userId: {}", userId);
        ShoppingCart cart = shoppingCartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> {
                    log.warn("Shopping cart not found for user ID: {}", userId);
                    return new ShoppingCartNotFoundException("Shopping cart not found for user ID: " + userId);
                });
        log.debug("Shopping cart ID: {} found for user ID: {}", cart.getCartId(), userId);

        // 3. Get all items from the cart. Filtering by a single branchId is removed for
        // consolidated order.
        Set<ShoppingCartItem> allCartItems = cart.getCartItems();

        if (allCartItems == null || allCartItems.isEmpty()) {
            log.warn("No items in shopping cart for user ID: {}", userId);
            throw new IllegalStateException(
                    "No items in the shopping cart for user ID: " + userId +
                            ". Please add items to your cart.");
        }
        log.debug("Found {} items in the user's cart.", allCartItems.size());

        // Create Order entity
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingMethod(shippingMethod);
        order.setOrderStatus(initialStatus);
        order.setPaymentType(paymentType);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setNotes(createOrderRequestDTO.getNotes());
        order.setShippingAddress(createOrderRequestDTO.getShippingAddress());

        BigDecimal subtotal = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        // Process all items from the cart
        for (ShoppingCartItem cartItem : allCartItems) {
            BranchInventory inventory = cartItem.getInventory();
            if (inventory == null) {
                log.error("Cart item ID {} is missing inventory information.", cartItem.getCartItemId());
                throw new IllegalStateException(
                        "Cart item ID " + cartItem.getCartItemId() + " is missing inventory information.");
            }
            Product product = inventory.getProduct();
            if (product == null) {
                log.error("Inventory ID {} (from cart item ID {}) is missing product information.",
                        inventory.getInventoryId(), cartItem.getCartItemId());
                throw new IllegalStateException(
                        "Inventory ID " + inventory.getInventoryId() + " is missing product information.");
            }

            // THÊM KIỂM TRA NULL CHO inventory.getBranch()
            if (inventory.getBranch() == null) {
                log.error("Inventory ID {} (from cart item ID {}) is missing branch information.",
                        inventory.getInventoryId(), cartItem.getCartItemId());
                throw new IllegalStateException(
                        "Inventory ID " + inventory.getInventoryId() + " is missing branch information.");
            }

            int quantity = cartItem.getQuantity();
            Integer currentQuantityInDb = inventory.getQuantityOnHand(); // Lấy số lượng hiện tại từ DB
            int currentStock = (currentQuantityInDb == null) ? 0 : currentQuantityInDb.intValue(); // Nếu null thì coi
                                                                                                   // là 0

            // Check stock using the inventory from the cart item (which is branch-specific)
            // Sử dụng currentStock đã được kiểm tra null
            if (currentStock < quantity) {
                log.warn("Insufficient stock for Product ID {} ({}) at Branch ID {}. Requested: {}, Available: {}",
                        product.getId(), product.getName(), inventory.getBranch().getBranchId(), quantity,
                        currentStock); // Sử dụng currentStock
                throw new InsufficientStockException(
                        "Insufficient stock for Product ID " + product.getId() + " (" + product.getName()
                                + ") at Branch ID " + inventory.getBranch().getBranchId() +
                                ". Requested: " + quantity + ", Available: " + currentStock); // Sử dụng currentStock
            }

            // THÊM KIỂM TRA NULL CHO inventory.getPrice()
            if (inventory.getPrice() == null) {
                log.error("Inventory ID {} (from cart item ID {}) is missing price information.",
                        inventory.getInventoryId(), cartItem.getCartItemId());
                throw new IllegalStateException(
                        "Inventory ID " + inventory.getInventoryId() + " is missing price information.");
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setInventory(inventory); // Inventory links to the specific branch
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(inventory.getPrice());
            BigDecimal itemSubtotal = inventory.getPrice().multiply(BigDecimal.valueOf(quantity));
            orderItem.setSubtotal(itemSubtotal);

            orderItems.add(orderItem);
            subtotal = subtotal.add(itemSubtotal);

            // Decrease stock
            // Sử dụng currentStock đã được kiểm tra null để tính toán
            inventory.setQuantityOnHand(currentStock - quantity);
            try {
                branchInventoryRepository.save(inventory);
                log.debug("Successfully updated inventory for product ID {} at branch ID {}. New quantity: {}",
                        product.getId(), inventory.getBranch().getBranchId(), inventory.getQuantityOnHand());
            } catch (org.springframework.dao.DataAccessException e) { // Catch Spring's DataAccessException for DB
                                                                      // errors
                log.error("Error saving updated BranchInventory for inventory ID {} (Product ID {}, Branch ID {}): {}",
                        inventory.getInventoryId(), product.getId(), inventory.getBranch().getBranchId(),
                        e.getMessage(), e);
                // Tùy thuộc vào hành vi mong muốn, bạn có thể ném lại một exception tùy chỉnh
                // hoặc xử lý nó (ví dụ: cố gắng rollback một phần đơn hàng nếu có thể, mặc dù
                // phức tạp)
                throw new RuntimeException("Failed to update stock for product ID " + product.getId() +
                        " at branch ID " + inventory.getBranch().getBranchId() + ". Order creation aborted.", e);
            }
        }

        // 5. Set items and calculated totals on the Order
        order.setOrderItems(orderItems);
        order.setSubtotalAmount(subtotal);
        BigDecimal shippingFee = shippingMethod.getBaseCost() != null ? shippingMethod.getBaseCost() : BigDecimal.ZERO;
        order.setShippingFee(shippingFee);

        BigDecimal calculatedDiscount = BigDecimal.ZERO;
        String promotionCode = createOrderRequestDTO.getPromotionCode();
        String effectiveAppliedPromotionCode = null;
        Promotion appliedPromotionEntity = null;

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
                    log.warn("Promotion code {} is not yet valid. Starts at {}", promotionCode,
                            promotion.getStartDate());
                    isValid = false;
                }
                if (isValid && promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
                    log.warn("Promotion code {} has expired. Ended at {}", promotionCode, promotion.getEndDate());
                    isValid = false;
                }
                if (isValid && promotion.getMinOrderValue() != null
                        && subtotal.compareTo(promotion.getMinOrderValue()) < 0) {
                    log.warn("Promotion code {} requires a minimum subtotal of {}. Current subtotal: {}", promotionCode,
                            promotion.getMinOrderValue(), subtotal);
                    isValid = false;
                }
                if (isValid && promotion.getTotalUsageLimit() != null
                        && promotion.getTotalUsedCount() >= promotion.getTotalUsageLimit()) {
                    log.warn("Promotion code {} has reached its total usage limit.", promotionCode);
                    isValid = false;
                }
                if (isValid) {
                    boolean branchApplicable = true;
                    // Vì Order entity không còn trường 'branch', order.getBranch() sẽ không còn tại
                    // hoặc luôn null.
                    // Đối với đơn hàng hợp nhất, nếu một khuyến mãi là dành riêng cho chi nhánh cụ
                    // thể (SPECIFIC_BRANCHES)
                    // hoặc có danh sách chi nhánh áp dụng, nó sẽ được coi là không áp dụng
                    // ở cấp độ toàn bộ đơn hàng, vì đơn hàng hợp nhất không gắn với một chi nhánh
                    // chính duy nhất.
                    if (promotion.getApplicableScope() == ApplicableScope.SPECIFIC_BRANCHES ||
                            (promotion.getBranches() != null && !promotion.getBranches().isEmpty())) {
                        log.warn(
                                "Promotion code {} is branch-specific. As this is a consolidated order not tied to a single primary branch, the promotion is not applied based on branch criteria for the overall order.",
                                promotionCode);
                        branchApplicable = false;
                    }
                    // Khối 'else' trước đó (kiểm tra order.getBranch()) đã được loại bỏ vì nó không
                    // còn cần thiết.

                    if (!branchApplicable) {
                        // Log cũ liên quan đến order.getBranch() != null cũng được loại bỏ.
                        isValid = false;
                    }
                }

                if (isValid) {
                    if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                        calculatedDiscount = subtotal.multiply(
                                promotion.getDiscountValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                        if (calculatedDiscount.compareTo(subtotal) > 0) { // Cap discount at subtotal
                            calculatedDiscount = subtotal;
                        }
                        log.info(
                                "Applied PERCENTAGE discount of {} for code {}. Original subtotal: {}, Discount value: {}%",
                                calculatedDiscount, promotionCode, subtotal, promotion.getDiscountValue());
                    } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                        calculatedDiscount = promotion.getDiscountValue();
                        if (calculatedDiscount.compareTo(subtotal) > 0) { // Cap discount at subtotal
                            calculatedDiscount = subtotal;
                        }
                        log.info(
                                "Applied FIXED_AMOUNT discount of {} for code {}. Original subtotal: {}, Discount value: {}",
                                calculatedDiscount, promotionCode, subtotal, promotion.getDiscountValue());
                    } else {
                        log.warn("Promotion code {} has an unsupported discount type: {}", promotionCode,
                                promotion.getDiscountType());
                    }

                    if (calculatedDiscount.compareTo(BigDecimal.ZERO) > 0) {
                        effectiveAppliedPromotionCode = promotion.getCode();
                        appliedPromotionEntity = promotion; // Store the entity

                        // Increment usage count for the promotion
                        appliedPromotionEntity.setTotalUsedCount(appliedPromotionEntity.getTotalUsedCount() + 1);

                        log.info("Promotion {} successfully applied with discount amount: {}",
                                effectiveAppliedPromotionCode, calculatedDiscount);
                    } else {
                        log.warn("Calculated discount for promotion code {} is zero or less. Not applying.",
                                promotionCode);
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
        if (createOrderRequestDTO.getShippingAddress() != null
                && !createOrderRequestDTO.getShippingAddress().isBlank()) {
            // Assuming Order entity has a shippingAddress field (String)
            order.setShippingAddress(createOrderRequestDTO.getShippingAddress()); // Use address from request DTO if
                                                                                  // provided
        } else if (user.getAddress() != null) {
            // Assuming Order entity has a shippingAddress field and User has getAddress()
            // returning String
            order.setShippingAddress(user.getAddress()); // Fallback to user's default address
        }
        // If neither is available, shippingAddress on the order will remain null

        // 6. Save Order (cascades to OrderItems)
        Order savedOrder = orderRepository.save(order);

        // If a promotion was successfully applied, update it and record its usage
        if (appliedPromotionEntity != null && calculatedDiscount.compareTo(BigDecimal.ZERO) > 0) {
            promotionRepository.save(appliedPromotionEntity); // Save the updated promotion (e.g., totalUsedCount)

            PromotionUsage promotionUsage = new PromotionUsage();
            promotionUsage.setPromotion(appliedPromotionEntity);
            promotionUsage.setOrder(savedOrder);
            promotionUsage.setUser(user);
            promotionUsage.setUsageDate(LocalDateTime.now());
            promotionUsage.setDiscountApplied(calculatedDiscount);
            promotionUsageRepository.save(promotionUsage);
            log.info("Promotion usage recorded for promotion ID {} and order ID {}",
                    appliedPromotionEntity.getPromotionId(), savedOrder.getOrderId());
        }

        // 7. Clear ONLY the processed items from the user's ShoppingCart
        log.debug("Deleting {} processed shopping cart items from cart ID: {}", allCartItems.size(),
                cart.getCartId());
        shoppingCartItemRepository.deleteAll(allCartItems);

        Order fetchedOrder = orderRepository.findById(savedOrder.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Failed to fetch the created order with ID: " + savedOrder.getOrderId()));

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

        // 3. Check if the order status allows cancellation
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == null || !CANCELLABLE_STATUSES.contains(currentStatus.getStatusName().toUpperCase())) {
            String statusName = (currentStatus != null) ? currentStatus.getStatusName() : "UNKNOWN";
            log.warn("Cancellation failed: Order ID {} has status '{}' which is not cancellable.", orderId, statusName);
            throw new IllegalStateException("Order cannot be cancelled because its current status is: " + statusName);
        }

        // 4. Restore stock for each item in the order
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                BranchInventory inventory = item.getInventory();
                if (inventory != null) {
                    inventory.setQuantityOnHand(inventory.getQuantityOnHand() + item.getQuantity());
                    branchInventoryRepository.save(inventory);
                    log.info("Restored {} unit(s) of product ID {} to inventory ID {} for branch ID {}",
                            item.getQuantity(), item.getProduct().getId(), inventory.getInventoryId(),
                            inventory.getBranch().getBranchId());
                } else {
                    log.warn(
                            "Could not restore stock for order item ID {}: BranchInventory link is missing. Product ID: {}",
                            item.getOrderItemId(), item.getProduct() != null ? item.getProduct().getId() : "N/A");
                }
            }
        }

        // 5. Potentially revert promotion usage
        if (order.getAppliedPromotionCode() != null) {
            promotionRepository.findByCode(order.getAppliedPromotionCode()).ifPresent(promotion -> {
                if (promotion.getTotalUsedCount() > 0) { // Check if it was actually used
                    promotion.setTotalUsedCount(promotion.getTotalUsedCount() - 1);
                    promotionRepository.save(promotion);
                    log.info("Decremented usage count for promotion code {} due to order cancellation.",
                            promotion.getCode());

                    // Remove PromotionUsage record
                    List<PromotionUsage> usages = promotionUsageRepository.findByOrderAndPromotion(order, promotion);
                    if (!usages.isEmpty()) {
                        promotionUsageRepository.deleteAll(usages);
                        log.info("Removed {} promotion usage record(s) for order ID {} and promotion code {}.",
                                usages.size(), order.getOrderId(), promotion.getCode());
                    }
                } else {
                    log.warn(
                            "Promotion code {} was applied to order ID {} but its totalUsedCount was already 0 or less. No decrement performed.",
                            promotion.getCode(), order.getOrderId());
                }
            });
        }

        Order savedCancelledOrder = orderRepository.save(order);
        log.info("Order ID {} successfully cancelled by user ID {}.", orderId, userId);

        return mapOrderToResponseDTO(savedCancelledOrder); // Sửa lỗi ở đây: đổi cancelledOrder thành
                                                           // savedCancelledOrder
    }

    private OrderResponseDTO mapOrderToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderCode(order.getOrderCode());

        // Map User info (handle potential null)
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getUserId());
            dto.setUserFullName(order.getUser().getFullName()); // Assuming User entity has getFullName()
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
        String currentStatus = (order.getOrderStatus() != null) ? order.getOrderStatus().getStatusName().toUpperCase()
                : "";
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
        // Simple unique code generator (e.g., "ORD-" + UUID)
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Các trạng thái cuối như DELIVERED, CANCELLED, RETURNED thường không cho phép
    // chuyển tiếp nữa
    // "DELIVERED", Set.of(),
    // "CANCELLED", Set.of()
    // );

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        log.info("Admin: Fetching all orders.");
        return orderRepository.findAll().stream()
                .map(this::mapOrderToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatusByAdmin(Integer orderId, String newStatusName) {
        log.info("Admin: Attempting to update status of order ID {} to {}", orderId, newStatusName);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Admin: Order not found with ID: {}", orderId);
                    return new EntityNotFoundException("Order not found with ID: " + orderId);
                });

        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == null || currentStatus.getStatusName() == null) {
            log.error("Admin: Order ID {} has a null or invalid current status.", orderId);
            throw new IllegalStateException("Order ID " + orderId + " has an invalid current status.");
        }
        String currentStatusName = currentStatus.getStatusName().toUpperCase();
        String targetStatusName = newStatusName.toUpperCase();

        // Kiểm tra xem trạng thái mới có hợp lệ không
        OrderStatus newOrderStatus = orderStatusRepository.findByStatusNameIgnoreCase(targetStatusName)
                .orElseThrow(() -> {
                    log.warn("Admin: Invalid target status name '{}' for order ID {}", targetStatusName, orderId);
                    return new IllegalArgumentException("Invalid order status: " + targetStatusName +
                            ". Valid statuses are: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, ON_HOLD, RETURNED.");
                });

        // Kiểm tra logic chuyển đổi trạng thái
        Set<String> allowedTransitions = VALID_STATUS_TRANSITIONS_BY_ADMIN.get(currentStatusName);
        if (allowedTransitions == null || !allowedTransitions.contains(targetStatusName)) {
            log.warn("Admin: Invalid status transition for order ID {} from {} to {}. Allowed transitions: {}",
                    orderId, currentStatusName, targetStatusName, allowedTransitions);
            throw new IllegalStateException(
                    "Cannot change order status from " + currentStatusName + " to " + targetStatusName +
                            ". Allowed transitions from " + currentStatusName + " are: "
                            + (allowedTransitions != null ? String.join(", ", allowedTransitions) : "None"));
        }

        // Không cho phép cập nhật trạng thái nếu đơn hàng đã ở trạng thái cuối cùng (ví
        // dụ: DELIVERED, CANCELLED)
        // trừ khi có logic nghiệp vụ đặc biệt.
        if (Set.of("DELIVERED", "CANCELLED", "RETURNED").contains(currentStatusName)) {
            log.warn("Admin: Order ID {} is already in a final state ({}) and cannot be updated further by admin.",
                    orderId, currentStatusName);
            throw new IllegalStateException(
                    "Order is already in a final state (" + currentStatusName + ") and cannot be updated.");
        }

        order.setOrderStatus(newOrderStatus);
        order.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian

        // Nếu chuyển sang 'DELIVERED', cập nhật paymentStatus thành 'PAID' nếu đang là
        // 'PENDING' và là COD
        if ("DELIVERED".equalsIgnoreCase(targetStatusName) &&
                order.getPaymentType() != null && "COD".equalsIgnoreCase(order.getPaymentType().getTypeName()) && // Giả
                                                                                                                  // sử
                                                                                                                  // có
                                                                                                                  // getTypeName()
                order.getPaymentStatus() == PaymentStatus.PENDING) {
            order.setPaymentStatus(PaymentStatus.PAID);
            log.info("Admin: Order ID {} status changed to DELIVERED. Payment status updated to PAID for COD.",
                    orderId);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Admin: Successfully updated status of order ID {} to {}", orderId, newOrderStatus.getStatusName());
        return mapOrderToResponseDTO(updatedOrder);
    }
}