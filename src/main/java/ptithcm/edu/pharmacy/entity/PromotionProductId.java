package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class PromotionProductId implements Serializable {
    private Integer promotionId;
    private Integer productId;
}