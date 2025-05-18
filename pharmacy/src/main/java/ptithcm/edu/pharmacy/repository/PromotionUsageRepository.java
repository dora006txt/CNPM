package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Order;
import ptithcm.edu.pharmacy.entity.Promotion;
import ptithcm.edu.pharmacy.entity.PromotionUsage;

import java.util.List;

@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    List<PromotionUsage> findByOrderAndPromotion(Order order, Promotion promotion);
}