package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.BranchInventoryDTO;
import ptithcm.edu.pharmacy.dto.ProductDisplayDTO; // Import the new DTO
import ptithcm.edu.pharmacy.service.BranchInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory") // Base path for inventory endpoints
public class BranchInventoryController {

    private final BranchInventoryService inventoryService;

    @Autowired
    public BranchInventoryController(BranchInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // GET /api/v1/inventory - Get all inventory items (potentially secured)
    @GetMapping
    public ResponseEntity<List<BranchInventoryDTO>> getAllInventory() {
        List<BranchInventoryDTO> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventoryList);
    }

    // GET /api/v1/inventory/{id} - Get specific inventory item by ID
    @GetMapping("/{id}")
    public ResponseEntity<BranchInventoryDTO> getInventoryById(@PathVariable Integer id) {
        return inventoryService.getInventoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/inventory/branch/{branchId} - Get inventory for a specific branch
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<BranchInventoryDTO>> getInventoryByBranch(@PathVariable Integer branchId) {
        List<BranchInventoryDTO> inventoryList = inventoryService.getInventoryByBranchId(branchId);
        return ResponseEntity.ok(inventoryList);
    }

    // GET /api/v1/inventory/product/{productId} - Get inventory for a specific product across all branches
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BranchInventoryDTO>> getInventoryByProduct(@PathVariable Integer productId) {
        List<BranchInventoryDTO> inventoryList = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventoryList);
    }

     // GET /api/v1/inventory/branch/{branchId}/product/{productId} - Get specific item by branch and product
    @GetMapping("/branch/{branchId}/product/{productId}")
    public ResponseEntity<BranchInventoryDTO> getInventoryByBranchAndProduct(
            @PathVariable Integer branchId, @PathVariable Integer productId) {
        return inventoryService.getInventoryByBranchAndProduct(branchId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/inventory/branch/{branchId}/products/display - Get product display info for a branch
    @GetMapping("/branch/{branchId}/products/display")
    public ResponseEntity<List<ProductDisplayDTO>> getProductDisplayByBranch(@PathVariable Integer branchId) {
        try {
            List<ProductDisplayDTO> displayList = inventoryService.getProductsForDisplayByBranch(branchId);
            return ResponseEntity.ok(displayList);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // NEW Endpoint: GET /api/v1/inventory/branch/{branchId}/product/{productId}/display
    @GetMapping("/branch/{branchId}/product/{productId}/display")
    public ResponseEntity<ProductDisplayDTO> getSingleProductDisplay(
            @PathVariable Integer branchId, @PathVariable Integer productId) {
        return inventoryService.getProductDisplayByBranchAndProduct(branchId, productId)
                .map(ResponseEntity::ok) // If present, return 200 OK with the DTO
                .orElse(ResponseEntity.notFound().build()); // If empty, return 404 Not Found
    }

    // POST /api/v1/inventory - Add a new inventory item (secured)
    @PostMapping
    public ResponseEntity<BranchInventoryDTO> addInventoryItem(@RequestBody BranchInventoryDTO inventoryDTO) {
        try {
            BranchInventoryDTO createdInventory = inventoryService.addInventory(inventoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInventory);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            // Handle cases where Branch or Product referenced in DTO doesn't exist
            return ResponseEntity.badRequest().body(null); // Or return a more specific error response
        }
    }

    // PUT /api/v1/inventory/{id} - Update an existing inventory item (secured)
    @PutMapping("/{id}")
    public ResponseEntity<BranchInventoryDTO> updateInventoryItem(@PathVariable Integer id, @RequestBody BranchInventoryDTO inventoryDTO) {
        try {
            BranchInventoryDTO updatedInventory = inventoryService.updateInventory(id, inventoryDTO);
            return ResponseEntity.ok(updatedInventory);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/inventory/{id} - Delete an inventory item (secured)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Integer id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.noContent().build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}