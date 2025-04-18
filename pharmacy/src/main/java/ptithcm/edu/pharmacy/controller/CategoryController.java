package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.CategoryRequest;
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.service.CategoryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            Category createdCategory = categoryService.createCategory(categoryRequest);
            // Return the created category and a 201 Created status
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            // Handle validation errors (e.g., missing name/slug)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            // Handle other errors (e.g., duplicate name/slug, parent not found)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            // Consider using a more specific status code if appropriate (e.g., 409 Conflict for duplicates)
            return ResponseEntity.badRequest().body(errorResponse); 
        } catch (Exception e) {
            // Catch unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the category.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Optional: Add endpoints for GET, PUT, DELETE later
}