package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.ShoppingCart;

import java.util.Optional;
import java.util.List; // Add this import

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    // Find by User ID AND Branch ID
    Optional<ShoppingCart> findByUser_UserIdAndBranch_BranchId(Integer userId, Integer branchId);

    // Add this method to find all carts for a user
    List<ShoppingCart> findByUser_UserId(Integer userId);
}