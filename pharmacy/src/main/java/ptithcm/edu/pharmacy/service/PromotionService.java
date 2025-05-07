package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.PromotionRequest;
import ptithcm.edu.pharmacy.dto.PromotionResponse;

import java.util.List;

public interface PromotionService {
    List<PromotionResponse> getAllPromotions();
    PromotionResponse getPromotionById(Integer id);
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Integer id, PromotionRequest request);
    void deletePromotion(Integer id); // Deactivates the promotion
    List<PromotionResponse> getActiveAndApplicablePromotions(); // For customer view
}