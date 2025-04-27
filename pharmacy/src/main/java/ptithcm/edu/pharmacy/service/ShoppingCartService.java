package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.AddToCartRequest;
import ptithcm.edu.pharmacy.dto.ShoppingCartDTO;

public interface ShoppingCartService {

    /**
     * Adds an item to the user's shopping cart or updates the quantity if the item already exists.
     *
     * @param userId The ID (Integer) of the user performing the action. // <-- Update comment
     * @param request The details of the item to add (branchId, productId, quantity).
     * @return The updated state of the shopping cart.
     * @throws jakarta.persistence.EntityNotFoundException if User, Product, Branch, or BranchInventory is not found.
     * @throws ptithcm.edu.pharmacy.service.exception.InsufficientStockException if the requested quantity exceeds available stock.
     */
    // Change String to Integer here
    ShoppingCartDTO addItemToCart(Integer userId, AddToCartRequest request);

    // Future methods:
    // ShoppingCartDTO getCartByUserId(String userId);
    // ShoppingCartDTO removeItemFromCart(String userId, Integer cartItemId);
    // ShoppingCartDTO updateItemQuantity(String userId, Integer cartItemId, Integer quantity);
    // void clearCart(String userId);
}