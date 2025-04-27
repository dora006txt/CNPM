package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.BranchInventory;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, Integer> {
    // Find inventory by branch ID
    List<BranchInventory> findByBranch_BranchId(Integer branchId);

    // Find inventory by product ID
    List<BranchInventory> findByProduct_Id(Integer productId);

    // Find specific inventory item by branch and product
    Optional<BranchInventory> findByBranch_BranchIdAndProduct_Id(Integer branchId, Integer productId);

    // You can add more specific query methods as needed, e.g., find by batch number, expiry date range, etc.
}