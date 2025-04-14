package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Promotion_Branches")
public class PromotionBranch {
    @EmbeddedId
    private PromotionBranchId id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("branchId")
    @JoinColumn(name = "branch_id")
    private Branch branch;
}