package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.BranchInventoryDTO;
import ptithcm.edu.pharmacy.dto.ProductDisplayDTO; // Import the new DTO
import java.util.List;
import java.util.Optional;

public interface BranchInventoryService {
    List<BranchInventoryDTO> getAllInventory();
    List<BranchInventoryDTO> getInventoryByBranchId(Integer branchId);
    List<BranchInventoryDTO> getInventoryByProductId(Integer productId);
    Optional<BranchInventoryDTO> getInventoryById(Integer inventoryId);
    Optional<BranchInventoryDTO> getInventoryByBranchAndProduct(Integer branchId, Integer productId);
    BranchInventoryDTO addInventory(BranchInventoryDTO inventoryDTO);
    BranchInventoryDTO updateInventory(Integer inventoryId, BranchInventoryDTO inventoryDTO);
    void deleteInventory(Integer inventoryId);

    // New method for product display
    List<ProductDisplayDTO> getProductsForDisplayByBranch(Integer branchId);

    // New method for single product display by branch and product ID
    Optional<ProductDisplayDTO> getProductDisplayByBranchAndProduct(Integer branchId, Integer productId);
}