package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Getter; // <-- Ensure this (or @Data) is present

@Getter // <-- Add this if missing
@Builder
public class CategoryResponse {
    private Integer id;
    private String name;
    private String slug;
}