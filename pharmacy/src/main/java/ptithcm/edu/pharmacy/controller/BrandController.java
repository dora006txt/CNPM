package ptithcm.edu.pharmacy.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.BrandRequest;
import ptithcm.edu.pharmacy.dto.BrandResponse; // Import BrandResponse
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.service.BrandService;

import java.util.HashMap;
import java.util.List; // Import List
import java.util.Map;
import java.util.stream.Collectors; // Import Collectors
import jakarta.persistence.EntityNotFoundException; // Import EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException; // Import DataIntegrityViolationException

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Helper method to map Brand entity to BrandResponse DTO
    private BrandResponse mapToBrandResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                // map other fields if any
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandRequest request) {
        try {
            Brand createdBrand = brandService.createBrand(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToBrandResponse(createdBrand)); // Return
                                                                                                     // BrandResponse
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = (e instanceof RuntimeException && e.getMessage().contains("already exists"))
                    ? HttpStatus.CONFLICT // 409 for duplicate
                    : HttpStatus.BAD_REQUEST; // 400 for other validation/argument issues
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            // Catch unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the brand.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // GET all brands
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();
        List<BrandResponse> brandResponses = brands.stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(brandResponses);
    }

    // GET brand by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Integer id) {
        try {
            Brand brand = brandService.getBrandById(id);
            return ResponseEntity.ok(mapToBrandResponse(brand));
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while retrieving the brand.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // PUT update brand by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Integer id, @Valid @RequestBody BrandRequest request) {
        try {
            Brand updatedBrand = brandService.updateBrand(id, request);
            return ResponseEntity.ok(mapToBrandResponse(updatedBrand));
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (RuntimeException e) { // Handles IllegalArgumentException and duplicate name RuntimeException
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = (e.getMessage().contains("already exists"))
                    ? HttpStatus.CONFLICT
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while updating the brand.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // DELETE brand by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Integer id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (DataIntegrityViolationException e) { // Catch if brand is associated with products
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 Conflict
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while deleting the brand.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Exception handler for validation errors (@Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    // Optional: Add endpoints for GET, PUT, DELETE later
}