package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.AddToCartRequest;
import ptithcm.edu.pharmacy.dto.ShoppingCartDTO;
import java.util.Optional;

public interface ShoppingCartService {
    ShoppingCartDTO addItemToCart(Integer userId, AddToCartRequest request);

    Optional<ShoppingCartDTO> getCartByUserId(Integer userId);

    ShoppingCartDTO updateItemQuantity(Integer userId, Integer cartItemId, int quantity);

    ShoppingCartDTO removeItemFromCart(Integer userId, Integer cartItemId);

}