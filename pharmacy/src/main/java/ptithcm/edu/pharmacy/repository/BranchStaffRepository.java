package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.BranchStaff;

import java.util.List;

@Repository
public interface BranchStaffRepository extends JpaRepository<BranchStaff, Integer> {
    // Find assignments by staff ID
    List<BranchStaff> findByStaff_StaffId(Integer staffId);

    // Find assignments by branch ID
    List<BranchStaff> findByBranch_BranchId(Integer branchId);

    // Find current assignment for a staff member at a specific branch
    List<BranchStaff> findByStaff_StaffIdAndBranch_BranchIdAndEndDateIsNull(Integer staffId, Integer branchId);
}