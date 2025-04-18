package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.CategoryRequest;
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.repository.CategoryRepository;

import java.time.LocalDateTime;

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
            throw new RuntimeException("Category with name '" + categoryRequest.getName() + "' already exists.");
        }
        if (categoryRepository.existsBySlug(categoryRequest.getSlug())) {
            throw new RuntimeException("Category with slug '" + categoryRequest.getSlug() + "' already exists.");
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setSlug(categoryRequest.getSlug());
        category.setImageUrl(categoryRequest.getImageUrl());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Handle parent category if provided
        if (categoryRequest.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category with ID " + categoryRequest.getParentCategoryId() + " not found."));
            category.setParentCategory(parentCategory);
        }

        // Save the new category to the database
        return categoryRepository.save(category);
    }

    // Optional: Add methods for getting, updating, deleting categories later
}