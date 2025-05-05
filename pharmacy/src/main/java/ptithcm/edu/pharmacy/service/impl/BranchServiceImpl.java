package ptithcm.edu.pharmacy.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.BranchDTO;
import ptithcm.edu.pharmacy.entity.Branch;
import ptithcm.edu.pharmacy.repository.BranchRepository;
import ptithcm.edu.pharmacy.service.BranchService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<BranchDTO> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        return branches.stream()
                .map(this::mapToBranchDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BranchDTO> getBranchById(Integer id) {
        return branchRepository.findById(id)
                .map(this::mapToBranchDTO);
    }

    @Override
    public BranchDTO createBranch(BranchDTO branchDTO) {
        Branch branch = mapToBranchEntity(branchDTO);
        // Ensure ID is null for creation to avoid accidental updates
        branch.setBranchId(null);
        Branch savedBranch = branchRepository.save(branch);
        return mapToBranchDTO(savedBranch);
    }

    @Override
    public BranchDTO updateBranch(Integer id, BranchDTO branchDTO) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + id));

        // Update fields from DTO
        existingBranch.setName(branchDTO.getName());
        existingBranch.setAddress(branchDTO.getAddress());
        existingBranch.setPhoneNumber(branchDTO.getPhoneNumber());
        existingBranch.setLatitude(branchDTO.getLatitude());
        existingBranch.setLongitude(branchDTO.getLongitude());
        existingBranch.setOperatingHours(branchDTO.getOperatingHours());
        existingBranch.setIsActive(branchDTO.getIsActive());
        // Update other fields as needed

        Branch updatedBranch = branchRepository.save(existingBranch);
        return mapToBranchDTO(updatedBranch);
    }

    @Override
    public void deleteBranch(Integer id) {
        if (!branchRepository.existsById(id)) {
            throw new EntityNotFoundException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }

    // Helper method to map Branch entity to BranchDTO
    private BranchDTO mapToBranchDTO(Branch branch) {
        BranchDTO dto = new BranchDTO();
        dto.setBranchId(branch.getBranchId());
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        dto.setPhoneNumber(branch.getPhoneNumber());
        dto.setLatitude(branch.getLatitude());
        dto.setLongitude(branch.getLongitude());
        dto.setOperatingHours(branch.getOperatingHours());
        dto.setIsActive(branch.getIsActive());
        return dto;
    }

    // Helper method to map BranchDTO to Branch entity
    private Branch mapToBranchEntity(BranchDTO dto) {
        Branch branch = new Branch();
        // Don't set ID here for creation, handle in update separately
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setPhoneNumber(dto.getPhoneNumber());
        branch.setLatitude(dto.getLatitude());
        branch.setLongitude(dto.getLongitude());
        branch.setOperatingHours(dto.getOperatingHours());
        branch.setIsActive(dto.getIsActive());
        return branch;
    }
}