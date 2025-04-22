package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Import ResponseStatusException
import ptithcm.edu.pharmacy.dto.CategoryRequest;
import ptithcm.edu.pharmacy.dto.CategoryResponse; // Import the Response DTO
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories") // <--- Ensures base path is /api/categories
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // --- Endpoint: Create Category ---
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        try {
            Category createdCategory = categoryService.createCategory(categoryRequest);
            CategoryResponse responseDto = mapCategoryToResponseDto(createdCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException | ResponseStatusException e) { // Catch specific exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            // Determine status code based on exception type if needed
            HttpStatus status = (e instanceof ResponseStatusException) ? HttpStatus.valueOf(((ResponseStatusException) e).getStatusCode().value()) : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) { // Catch broader exceptions last
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the category.");
            // Log the exception e.printStackTrace(); or use a logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // --- End Create Category ---

    // --- Endpoint: Get All Categories ---
    @GetMapping // Handles GET requests at /api/categories
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        List<CategoryResponse> responseDtos = categories.stream()
                .map(this::mapCategoryToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }
    // --- End Get All Categories ---

    // --- Endpoint: Get Category by ID ---
    @GetMapping("/{id}") // Handles GET requests at /api/categories/{id}
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
         try {
            Category category = categoryService.findCategoryById(id);
            CategoryResponse responseDto = mapCategoryToResponseDto(category);
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while fetching the category.");
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // --- End Get Category by ID ---


    // --- Endpoint: Update Category ---
    @PutMapping("/{id}") // Handles PUT requests at /api/categories/{id}
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody CategoryRequest categoryRequest) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
            CategoryResponse responseDto = mapCategoryToResponseDto(updatedCategory);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException | ResponseStatusException e) { // Catch specific exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = (e instanceof ResponseStatusException) ? HttpStatus.valueOf(((ResponseStatusException) e).getStatusCode().value()) : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) { // Catch broader exceptions last
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while updating the category.");
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // --- End Update Category ---

    // --- Endpoint: Delete Category ---
    @DeleteMapping("/{id}") // Handles DELETE requests at /api/categories/{id}
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (ResponseStatusException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        } catch (Exception e) { // Catch broader exceptions last
             Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while deleting the category.");
             // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // --- End Delete Category ---


    // --- Helper mapping function ---
    private CategoryResponse mapCategoryToResponseDto(Category category) {
        if (category == null) return null;
        // Basic mapping, doesn't include parent/children details for simplicity here
        return CategoryResponse.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImage_url())
                // Add parentCategoryId if needed in the response
                // .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null)
                .build();
    }
    // --- End Helper mapping function ---

    // Optional: Add Exception Handlers if needed (e.g., using @ControllerAdvice)
}