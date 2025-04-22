package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; // Import ResponseStatusException
import org.springframework.http.HttpStatus; // Import HttpStatus
import ptithcm.edu.pharmacy.dto.CategoryRequest;
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List; // Add this import
import java.util.Objects; // Import Objects for comparison

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional // Ensure atomicity
    public Category createCategory(CategoryRequest categoryRequest) {
        // Validate input (e.g., check for null or empty required fields)
        if (categoryRequest.getName() == null || categoryRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (categoryRequest.getSlug() == null || categoryRequest.getSlug().trim().isEmpty()) {
            throw new IllegalArgumentException("Category slug cannot be empty.");
        }

        // Check for duplicate name or slug
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name '" + categoryRequest.getName() + "' already exists.");
        }
        if (categoryRepository.existsBySlug(categoryRequest.getSlug())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with slug '" + categoryRequest.getSlug() + "' already exists.");
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setSlug(categoryRequest.getSlug());
        category.setImage_url(categoryRequest.getImageUrl());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Handle parent category if provided
        if (categoryRequest.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category with ID " + categoryRequest.getParentCategoryId() + " not found."));
            category.setParentCategory(parentCategory);
        }

        // Save the new category to the database
        return categoryRepository.save(category);
    }

    // --- New Method: Update Category ---
    @Transactional
    public Category updateCategory(Integer id, CategoryRequest categoryRequest) {
        // 1. Find the existing category
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));

        // 2. Validate input
        if (categoryRequest.getName() == null || categoryRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (categoryRequest.getSlug() == null || categoryRequest.getSlug().trim().isEmpty()) {
            throw new IllegalArgumentException("Category slug cannot be empty.");
        }

        // 3. Check for duplicate name (if changed)
        if (!existingCategory.getName().equalsIgnoreCase(categoryRequest.getName())) {
            if (categoryRepository.existsByName(categoryRequest.getName())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name '" + categoryRequest.getName() + "' already exists.");
            }
            existingCategory.setName(categoryRequest.getName());
        }

        // 4. Check for duplicate slug (if changed)
        if (!existingCategory.getSlug().equalsIgnoreCase(categoryRequest.getSlug())) {
             if (categoryRepository.existsBySlug(categoryRequest.getSlug())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with slug '" + categoryRequest.getSlug() + "' already exists.");
             }
             existingCategory.setSlug(categoryRequest.getSlug());
        }

        // 5. Update other fields
        existingCategory.setDescription(categoryRequest.getDescription());
        existingCategory.setImage_url(categoryRequest.getImageUrl());
        // updatedAt will be handled by @PreUpdate

        // 6. Handle parent category update
        Integer requestedParentId = categoryRequest.getParentCategoryId();
        Integer currentParentId = (existingCategory.getParentCategory() != null) ? existingCategory.getParentCategory().getCategoryId() : null;

        if (!Objects.equals(currentParentId, requestedParentId)) {
            if (requestedParentId != null) {
                // Prevent setting category as its own parent
                if (Objects.equals(requestedParentId, existingCategory.getCategoryId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category cannot be its own parent.");
                }
                Category parentCategory = categoryRepository.findById(requestedParentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category with ID " + requestedParentId + " not found."));
                existingCategory.setParentCategory(parentCategory);
            } else {
                existingCategory.setParentCategory(null); // Remove parent
            }
        }

        // 7. Save the updated category
        return categoryRepository.save(existingCategory);
    }
    // --- End Update Category ---

    // --- New Method: Delete Category ---
    @Transactional
    public void deleteCategory(Integer id) {
        // 1. Find the category first to check associations
        Category categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));

        // 2. Check if the category has associated products
        // Eagerly fetch products size to avoid lazy loading issues within the check
        // Note: Ensure the 'products' relationship in Category entity is fetched appropriately
        // If it's LAZY, this access might trigger another query. Consider a dedicated query if performance is critical.
        if (categoryToDelete.getProducts() != null && !categoryToDelete.getProducts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete category with ID " + id + " because it has associated products.");
        }

        // Optional: Add check for subcategories if needed
        // if (categoryToDelete.getSubCategories() != null && !categoryToDelete.getSubCategories().isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot delete category with ID " + id + " because it has subcategories.");
        // }


        // 3. If no associations blocking deletion, proceed to delete
        categoryRepository.delete(categoryToDelete); // Use delete(entity) or deleteById(id)
    }
    // --- End Delete Category ---

    // --- Method: Find All Categories ---
    @Transactional(readOnly = true) // Use readOnly for query operations
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
    // --- End Find All Categories ---

    // --- New Method: Find Category By ID ---
    @Transactional(readOnly = true)
    public Category findCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }
    // --- End Find Category By ID ---

    // Optional: Add methods for getting, updating, deleting categories later
}