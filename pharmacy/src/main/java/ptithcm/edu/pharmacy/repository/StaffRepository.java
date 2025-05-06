package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Staff;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    // Find staff by user ID
    Optional<Staff> findByUser_UserId(Integer userId);

    // Find staff by branch ID
    java.util.List<Staff> findByBranch_BranchId(Integer branchId);

    // Check if staff exists by user ID
    boolean existsByUser_UserId(Integer userId);
}