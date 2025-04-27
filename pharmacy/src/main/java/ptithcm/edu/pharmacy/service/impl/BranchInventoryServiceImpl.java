package ptithcm.edu.pharmacy.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.BranchInventoryDTO;
import ptithcm.edu.pharmacy.entity.Branch;
import ptithcm.edu.pharmacy.entity.BranchInventory;
import ptithcm.edu.pharmacy.entity.Product;
import ptithcm.edu.pharmacy.repository.BranchInventoryRepository;
import ptithcm.edu.pharmacy.repository.BranchRepository;
import ptithcm.edu.pharmacy.repository.ProductRepository;
import ptithcm.edu.pharmacy.service.BranchInventoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// --- Added missing imports ---
import ptithcm.edu.pharmacy.dto.ProductDisplayDTO;
import ptithcm.edu.pharmacy.entity.Category;
// --- End added imports ---

@Service
public class BranchInventoryServiceImpl implements BranchInventoryService {

    private final BranchInventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    @Autowired
    public BranchInventoryServiceImpl(BranchInventoryRepository inventoryRepository,
                                      BranchRepository branchRepository,
                                      ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<BranchInventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchInventoryDTO> getInventoryByBranchId(Integer branchId) {
        return inventoryRepository.findByBranch_BranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchInventoryDTO> getInventoryByProductId(Integer productId) {
        return inventoryRepository.findByProduct_Id(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

     @Override
    public Optional<BranchInventoryDTO> getInventoryById(Integer inventoryId) {
        return inventoryRepository.findById(inventoryId).map(this::mapToDTO);
    }

    // --- Added missing method implementation from interface ---
    @Override
    public Optional<BranchInventoryDTO> getInventoryByBranchAndProduct(Integer branchId, Integer productId) {
        return inventoryRepository.findByBranch_BranchIdAndProduct_Id(branchId, productId)
                .map(this::mapToDTO);
    }
    // --- End added method ---


    // New method implementation for single product display
    @Override
    public Optional<ProductDisplayDTO> getProductDisplayByBranchAndProduct(Integer branchId, Integer productId) {
        // Find the specific inventory item
        Optional<BranchInventory> inventoryOpt = inventoryRepository.findByBranch_BranchIdAndProduct_Id(branchId, productId);

        // Map to DTO if found
        return inventoryOpt.map(this::mapToProductDisplayDTO);
    }

    // --- Added missing method implementation from interface ---
    @Override
    public List<ProductDisplayDTO> getProductsForDisplayByBranch(Integer branchId) {
        // Verify branch exists (optional but good practice)
        if (!branchRepository.existsById(branchId)) {
            throw new EntityNotFoundException("Branch not found with id: " + branchId);
        }

        List<BranchInventory> inventoryList = inventoryRepository.findByBranch_BranchId(branchId);
        return inventoryList.stream()
                .map(this::mapToProductDisplayDTO)
                .collect(Collectors.toList());
    }
    // --- End added method ---

    @Override
    public BranchInventoryDTO addInventory(BranchInventoryDTO inventoryDTO) {
        BranchInventory inventory = mapToEntity(inventoryDTO);
        inventory.setInventoryId(null); // Ensure creation
        inventory.setLastUpdated(LocalDateTime.now()); // <-- This line sets the current time
        BranchInventory savedInventory = inventoryRepository.save(inventory);
        return mapToDTO(savedInventory);
    }

    @Override
    public BranchInventoryDTO updateInventory(Integer inventoryId, BranchInventoryDTO inventoryDTO) {
        BranchInventory existingInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found with id: " + inventoryId));

        // Update fields - Consider which fields should be updatable
        // Usually, you update quantity, price, location, maybe expiry/batch if correcting an error.
        // Re-assigning branch or product might be complex and better handled by deleting/re-adding.
        existingInventory.setQuantityOnHand(inventoryDTO.getQuantityOnHand());
        existingInventory.setPrice(inventoryDTO.getPrice());
        existingInventory.setDiscountPrice(inventoryDTO.getDiscountPrice());
        existingInventory.setExpiryDate(inventoryDTO.getExpiryDate());
        existingInventory.setBatchNumber(inventoryDTO.getBatchNumber());
        existingInventory.setLocationInStore(inventoryDTO.getLocationInStore());
        existingInventory.setLastUpdated(LocalDateTime.now());

        // Optionally re-fetch Branch and Product if their IDs might change in the DTO,
        // though this is less common for an inventory update.
        // Branch branch = branchRepository.findById(inventoryDTO.getBranchId())
        //         .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + inventoryDTO.getBranchId()));
        // Product product = productRepository.findById(inventoryDTO.getProductId())
        //         .orElseThrow(() -> new EntityNotFoundException("Product not found: " + inventoryDTO.getProductId()));
        // existingInventory.setBranch(branch);
        // existingInventory.setProduct(product);

        BranchInventory updatedInventory = inventoryRepository.save(existingInventory);
        return mapToDTO(updatedInventory);
    }

    @Override
    public void deleteInventory(Integer inventoryId) {
        if (!inventoryRepository.existsById(inventoryId)) {
            throw new EntityNotFoundException("Inventory not found with id: " + inventoryId);
        }
        inventoryRepository.deleteById(inventoryId);
    }

    // --- Helper Methods ---

    // --- Added missing helper method ---
    private BranchInventoryDTO mapToDTO(BranchInventory inventory) {
        BranchInventoryDTO dto = new BranchInventoryDTO();
        dto.setInventoryId(inventory.getInventoryId());
        dto.setQuantityOnHand(inventory.getQuantityOnHand());
        dto.setPrice(inventory.getPrice());
        dto.setDiscountPrice(inventory.getDiscountPrice());
        dto.setExpiryDate(inventory.getExpiryDate());
        dto.setBatchNumber(inventory.getBatchNumber());
        dto.setLocationInStore(inventory.getLocationInStore());
        dto.setLastUpdated(inventory.getLastUpdated());

        if (inventory.getBranch() != null) {
            dto.setBranchId(inventory.getBranch().getBranchId());
            // dto.setBranchName(inventory.getBranch().getName()); // Optionally include name
        }
        if (inventory.getProduct() != null) {
            dto.setProductId(inventory.getProduct().getId());
             // dto.setProductName(inventory.getProduct().getName()); // Optionally include name
        }
        return dto;
    }
    // --- End added helper method ---

    // --- Added missing helper method ---
    private BranchInventory mapToEntity(BranchInventoryDTO dto) {
        BranchInventory inventory = new BranchInventory();

        // Fetch related entities
        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found with id: " + dto.getBranchId()));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

        inventory.setBranch(branch);
        inventory.setProduct(product);
        inventory.setQuantityOnHand(dto.getQuantityOnHand());
        inventory.setPrice(dto.getPrice());
        inventory.setDiscountPrice(dto.getDiscountPrice());
        inventory.setExpiryDate(dto.getExpiryDate());
        inventory.setBatchNumber(dto.getBatchNumber());
        inventory.setLocationInStore(dto.getLocationInStore());
        // inventoryId and lastUpdated are set during add/update logic

        return inventory;
    }
    // --- End added helper method ---


    // Helper method to map to ProductDisplayDTO (ensure it exists from previous steps)
    // --- Added missing helper method ---
    private ProductDisplayDTO mapToProductDisplayDTO(BranchInventory inventory) {
        ProductDisplayDTO dto = new ProductDisplayDTO();
        Product product = inventory.getProduct();
        Branch branch = inventory.getBranch();

        // Product Info
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setImageUrl(product.getImageUrl());
            Category category = product.getCategory(); // Use the imported Category
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        // Branch Info
        if (branch != null) {
            dto.setBranchId(branch.getBranchId());
            dto.setBranchName(branch.getName()); // Assuming Branch entity has getName()
        }

        // Branch Inventory Info
        dto.setInventoryId(inventory.getInventoryId());
        dto.setQuantityOnHand(inventory.getQuantityOnHand());
        dto.setPrice(inventory.getPrice());
        dto.setDiscountPrice(inventory.getDiscountPrice());
        dto.setLocationInStore(inventory.getLocationInStore());

        return dto;
    }
    // --- End added helper method ---
}