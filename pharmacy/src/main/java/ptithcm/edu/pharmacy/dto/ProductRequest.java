package ptithcm.edu.pharmacy.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ptithcm.edu.pharmacy.entity.ProductStatus; // Import the enum

// Removed BigDecimal import
// import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 200, message = "Product name max length is 200 characters")
    private String name;

    // Slug will be generated from name in the service, removed from request

    @Size(max = 100, message = "SKU max length is 100 characters")
    private String sku; // Optional based on SQL (nullable)

    private String description;
    private String ingredients;
    private String usageInstructions;
    private String contraindications;
    private String sideEffects;
    private String storageConditions;
    private String packaging;
    private String unit;

    @Size(max = 255, message = "Image URL max length is 255 characters")
    private String imageUrl; // Optional

    // Removed price and stockQuantity
    // @NotNull(message = "Price cannot be null") ...
    // private BigDecimal price;
    // @NotNull(message = "Stock quantity cannot be null") ...
    // private Integer stockQuantity;

    // Changed to nullable based on SQL schema
    private Integer categoryId;
    private Integer brandId;
    private Integer manufacturerId;

    // Added fields based on entity changes
    private Boolean isPrescriptionRequired; // Optional, defaults to false in entity/service
    private ProductStatus status; // Optional, defaults to ACTIVE in entity/service
}