package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Promotion_Products")
public class PromotionProduct {
    @EmbeddedId
    private PromotionProductId id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}