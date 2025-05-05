package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.BranchInventory;
// import ptithcm.edu.pharmacy.entity.BranchInventoryId; // Removed unused import

import java.util.List; // Added import for List
import java.util.Optional;

@Repository
// Changed BranchInventoryId to Integer to match the entity's primary key type
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, Integer> {

    // Method used in OrderServiceImpl
    Optional<BranchInventory> findByBranch_BranchIdAndProduct_Id(Integer branchId, Integer productId);

    // Added methods used in BranchInventoryServiceImpl
    List<BranchInventory> findByBranch_BranchId(Integer branchId);
    List<BranchInventory> findByProduct_Id(Integer productId);

    // Add other necessary methods if needed in the future
}