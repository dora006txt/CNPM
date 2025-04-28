package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.AddToCartRequest;
import ptithcm.edu.pharmacy.dto.ShoppingCartDTO;
// Remove the incorrect import: import ptithcm.edu.pharmacy.dto.request.AddToCartRequest;
import java.util.List; // Add this import

public interface ShoppingCartService {
    // Change String username to Integer userId
    ShoppingCartDTO addItemToCart(Integer userId, AddToCartRequest request);

    List<ShoppingCartDTO> getCartsByUserId(Integer userId); // Add this method

    // Add the missing method signatures
    ShoppingCartDTO updateItemQuantity(Integer userId, Integer cartItemId, int quantity);

    ShoppingCartDTO removeItemFromCart(Integer userId, Integer cartItemId);

    // Future methods:
    // ShoppingCartDTO getCartByUserId(String userId);
    // void clearCart(String userId);
}