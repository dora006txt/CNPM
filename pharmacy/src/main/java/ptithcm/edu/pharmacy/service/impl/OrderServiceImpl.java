package ptithcm.edu.pharmacy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.CreateOrderRequestDTO;
import ptithcm.edu.pharmacy.dto.OrderItemResponseDTO;
import ptithcm.edu.pharmacy.dto.OrderResponseDTO;
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.service.OrderService;
import ptithcm.edu.pharmacy.service.exception.InsufficientStockException;
import ptithcm.edu.pharmacy.service.exception.ShoppingCartNotFoundException; // <-- Add this import

import jakarta.persistence.EntityNotFoundException; // Ensure correct import
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List; // Import List
import org.springframework.security.access.AccessDeniedException; // For authorization check
import org.slf4j.Logger; // Add logger import
import org.slf4j.LoggerFactory; 
@Service
@RequiredArgsConstructor // Lombok annotation for constructor injection of final fields
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository; // Injected here
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository; // Need this to delete items
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class); // Add logger instance


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

        OrderStatus initialStatus = orderStatusRepository.findByStatusName("PENDING")
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
        order.setDiscountAmount(BigDecimal.ZERO); // TODO: Implement discount logic
        order.setFinalAmount(subtotal.add(shippingFee).subtract(order.getDiscountAmount()));

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

        // Option 2: Clear the collection and update the cart (if cascade remove is set on ShoppingCart entity)
        // cart.getCartItems().clear();
        // shoppingCartRepository.save(cart);

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
    // ... existing checks ...
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

        // --- Authorization Check ---
        // Check if the user requesting the order is the one who placed it
        if (!order.getUser().getUserId().equals(userId)) {
            // Optional: Add logic here to check if the requesting user is an ADMIN
            // Example:
            // User requestingUser = userRepository.findById(userId).orElse(null);
            // if (requestingUser == null || !requestingUser.getRole().getName().equals("ADMIN")) {
                 throw new AccessDeniedException("User " + userId + " is not authorized to view order " + orderId);
            // }
        }
        // --- End Authorization Check ---

        return mapOrderToResponseDTO(order);
    }
    // --- End of findOrderById implementation ---


    // --- Updated mapOrderToResponseDTO with more logging and error handling ---
    private OrderResponseDTO mapOrderToResponseDTO(Order order) {
        if (order == null) {
            log.warn("Attempted to map a null Order object."); // Log if order is null
            return null;
        }
        OrderResponseDTO dto = new OrderResponseDTO();
        try { // Wrap mapping logic in try-catch
            dto.setOrderId(order.getOrderId());
            dto.setOrderCode(order.getOrderCode());

            // User details
            User user = order.getUser();
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setUserFullName(user.getFullName()); // Verify User has getFullName()
            } else {
                log.warn("Order ID {} has a null User.", order.getOrderId());
            }

            // Branch details
            Branch branch = order.getBranch();
            if (branch != null) {
                dto.setBranchId(branch.getBranchId());
                dto.setBranchName(branch.getName()); // Verify Branch has getName()
            } else {
                log.warn("Order ID {} has a null Branch.", order.getOrderId());
            }

            dto.setOrderDate(order.getOrderDate());

            // Shipping details
            ShippingMethod shippingMethod = order.getShippingMethod();
            if (shippingMethod != null) {
                dto.setShippingMethodId(shippingMethod.getMethodId());
                dto.setShippingMethodName(shippingMethod.getName()); // Verify ShippingMethod has getName()
            } else {
                log.warn("Order ID {} has a null ShippingMethod.", order.getOrderId());
            }

            // Order Status details
            OrderStatus orderStatus = order.getOrderStatus();
            if (orderStatus != null) {
                dto.setOrderStatusId(orderStatus.getStatusId());
                dto.setOrderStatusName(orderStatus.getStatusName()); // Verify OrderStatus has getStatusName()
            } else {
                log.warn("Order ID {} has a null OrderStatus.", order.getOrderId());
            }

            // Payment details
            PaymentType paymentType = order.getPaymentType();
            if (paymentType != null) {
                dto.setPaymentTypeId(paymentType.getPaymentTypeId());
                dto.setPaymentTypeName(paymentType.getTypeName()); // Verify PaymentType has getTypeName()
            } else {
                log.warn("Order ID {} has a null PaymentType.", order.getOrderId());
            }
            dto.setPaymentStatus(order.getPaymentStatus());

            dto.setNotes(order.getNotes());
            dto.setRequiresConsultation(order.getRequiresConsultation());

            // Assigned Staff details
            Staff assignedStaff = order.getAssignedStaff();
            if (assignedStaff != null) {
                dto.setAssignedStaffId(assignedStaff.getStaffId()); // Verify Staff has getStaffId()
                dto.setAssignedStaffName(assignedStaff.getFullName()); // Verify Staff has getFullName()
            } else {
                // Staff might not be assigned yet, this is likely okay. Log as debug if needed.
                // log.debug("Order ID {} has no assigned staff.", order.getOrderId());
                dto.setAssignedStaffId(null);
                dto.setAssignedStaffName(null);
            }

            dto.setConsultationStatus(order.getConsultationStatus());

            // Amounts
            dto.setSubtotalAmount(order.getSubtotalAmount());
            dto.setShippingFee(order.getShippingFee());
            dto.setDiscountAmount(order.getDiscountAmount());
            dto.setFinalAmount(order.getFinalAmount());

            // Shipping Address
            dto.setShippingAddress(order.getShippingAddress()); // Verify Order has getShippingAddress()

            // Timestamps
            dto.setCreatedAt(order.getCreatedAt());
            dto.setUpdatedAt(order.getUpdatedAt());

            // Order Items
            if (order.getOrderItems() != null) {
                dto.setOrderItems(order.getOrderItems().stream()
                        .map(this::mapOrderItemToResponseDTO) // Calls the updated item mapper below
                        .collect(Collectors.toList()));
            } else {
                log.warn("Order ID {} has null OrderItems collection.", order.getOrderId());
                dto.setOrderItems(Collections.emptyList()); // Use empty list instead of null
            }

        } catch (Exception e) {
            // Log the exception during mapping
            log.error("Exception during mapping for Order ID {}: {}", order.getOrderId(), e.getMessage(), e);
            // Rethrow as a runtime exception to signal a server error (should lead to 500)
            throw new RuntimeException("Failed to map Order to DTO for Order ID: " + order.getOrderId(), e);
        }
        return dto;
    }

    // --- Updated mapOrderItemToResponseDTO with error handling ---
    private OrderItemResponseDTO mapOrderItemToResponseDTO(OrderItem item) {
        if (item == null) {
            log.warn("Attempted to map a null OrderItem.");
            return null;
        }
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        try { // Wrap mapping logic in try-catch
            dto.setOrderItemId(item.getOrderItemId());

            Product product = item.getProduct();
            if (product != null) {
                dto.setProductId(product.getId());
                dto.setProductName(product.getName()); // Verify Product has getName()
            } else {
                log.warn("OrderItem ID {} has a null Product.", item.getOrderItemId());
            }

            dto.setQuantity(item.getQuantity());
            dto.setPriceAtPurchase(item.getPriceAtPurchase());
            dto.setSubtotal(item.getSubtotal());
        } catch (Exception e) {
            // Log the exception during mapping
            log.error("Exception during mapping for OrderItem ID {}: {}", item.getOrderItemId(), e.getMessage(), e);
            // Rethrow as a runtime exception
            throw new RuntimeException("Failed to map OrderItem to DTO for OrderItem ID: " + item.getOrderItemId(), e);
        }
        return dto;
    }
    // --- End Helper Methods ---


    private String generateOrderCode() {
        // Simple unique code generation (can be improved)
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}