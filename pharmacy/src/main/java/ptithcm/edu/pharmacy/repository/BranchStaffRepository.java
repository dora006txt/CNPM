package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Staff;

import java.util.List;

@Repository
public interface BranchStaffRepository extends JpaRepository<Staff, Integer> {
    // Tìm Staff bằng staffId của chính nó
    List<Staff> findByStaffId(Integer staffId);

    // Tìm Staff bằng branchId (thông qua thuộc tính 'branch' của Staff)
    List<Staff> findByBranch_BranchId(Integer branchId);

    // Tìm Staff bằng staffId và branchId.
    // Lưu ý: Phần "AndEndDateIsNull" đã được loại bỏ vì entity Staff
    // dường như không có thuộc tính 'endDate'.
    // Nếu bạn cần lọc theo 'endDate', hãy đảm bảo entity Staff có thuộc tính này
    // và tên phương thức phản ánh đúng (ví dụ: findByStaffIdAndBranch_BranchIdAndEndDateIsNull).
    List<Staff> findByStaffIdAndBranch_BranchId(Integer staffId, Integer branchId);
    
    // Nếu bạn thực sự cần một phương thức như findByStaff_StaffIdAndBranch_BranchIdAndEndDateIsNull
    // và 'endDate' là một trường trong Staff, nó sẽ là:
    // List<Staff> findByStaffIdAndBranch_BranchIdAndEndDateIsNull(Integer staffId, Integer branchId);
}