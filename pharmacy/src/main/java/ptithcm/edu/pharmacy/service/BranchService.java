package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.BranchDTO;
import java.util.List;
import java.util.Optional; // Import Optional

public interface BranchService {
    List<BranchDTO> getAllBranches();
    Optional<BranchDTO> getBranchById(Integer id); // Good practice to return Optional
    BranchDTO createBranch(BranchDTO branchDTO);
    BranchDTO updateBranch(Integer id, BranchDTO branchDTO);
    void deleteBranch(Integer id);
}