package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(nullable = false)
    private String name;

    private String sku;
    private String description;
    private String ingredients;

    @Column(name = "usage_instructions")
    private String usageInstructions;

    private String contraindications;

    @Column(name = "side_effects")
    private String sideEffects;

    @Column(name = "storage_conditions")
    private String storageConditions;

    private String packaging;
    private String unit;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(name = "is_prescription_required")
    private Boolean isPrescriptionRequired = false;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "average_rating")
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private Set<ProductImage> images;
}