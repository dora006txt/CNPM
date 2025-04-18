package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String description;
    private Integer parentCategoryId; // Optional: ID of the parent category
    private String slug;
    private String imageUrl; // Optional
}