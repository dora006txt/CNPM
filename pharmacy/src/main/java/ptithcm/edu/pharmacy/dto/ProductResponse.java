package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Getter; // <--- Make sure this is present
import ptithcm.edu.pharmacy.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter // <--- Or @Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private String ingredients;
    private String usageInstructions;
    private String contraindications;
    private String sideEffects;
    private String storageConditions;
    private String packaging;
    private String unit;
    private String imageUrl;
    private Boolean isPrescriptionRequired;
    private ProductStatus status;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Embed related DTOs
    private CategoryResponse category;
    private BrandResponse brand;
    private ManufacturerResponse manufacturer;
}