package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "Categories") // Make sure this table name is correct
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id") // Verify this column name
    private Integer categoryId;

    @Column(nullable = false) // Verify 'name' column name if needed
    private String name;

    // --- Verify these fields and their corresponding DB columns ---
    private String description; // Is the DB column name 'description'?
    private String image_url;    // Is the DB column name 'image_url'? Or something else?
    private String slug;        // Is the DB column name 'slug'?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private Set<Category> subCategories;

    @OneToMany(mappedBy = "category")
    private Set<Product> products;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // --- End Verification ---
}