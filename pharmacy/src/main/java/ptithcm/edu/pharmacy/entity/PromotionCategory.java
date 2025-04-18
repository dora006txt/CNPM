package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Promotion_Categories")
public class PromotionCategory {
    @EmbeddedId
    private PromotionCategoryId id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
}