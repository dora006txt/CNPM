package ptithcm.edu.pharmacy.controller;

import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Import more annotations
import ptithcm.edu.pharmacy.dto.BranchDTO;
import ptithcm.edu.pharmacy.service.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    // GET /api/v1/branches - Get all branches (Public)
    @GetMapping
    public ResponseEntity<List<BranchDTO>> getAllBranches() {
        List<BranchDTO> branches = branchService.getAllBranches();
        return ResponseEntity.ok(branches);
    }

    // GET /api/v1/branches/{id} - Get branch by ID (Could be public or secured)
    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Integer id) {
        return branchService.getBranchById(id)
                .map(ResponseEntity::ok) // If found, return 200 OK with body
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    // POST /api/v1/branches - Create a new branch (Admin only)
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchDTO branchDTO) {
        BranchDTO createdBranch = branchService.createBranch(branchDTO);
        // Return 201 Created status with the created branch DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBranch);
    }

    // PUT /api/v1/branches/{id} - Update an existing branch (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable Integer id, @RequestBody BranchDTO branchDTO) {
        try {
            BranchDTO updatedBranch = branchService.updateBranch(id, branchDTO);
            return ResponseEntity.ok(updatedBranch);
        } catch (jakarta.persistence.EntityNotFoundException e) { // Catch specific exception
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/branches/{id} - Delete a branch (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Integer id) {
        try {
            branchService.deleteBranch(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (jakarta.persistence.EntityNotFoundException e) { // Catch specific exception
            return ResponseEntity.notFound().build();
        }
    }
}