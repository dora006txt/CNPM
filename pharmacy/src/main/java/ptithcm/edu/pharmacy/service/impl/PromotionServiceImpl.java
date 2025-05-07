package ptithcm.edu.pharmacy.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptithcm.edu.pharmacy.dto.CategoryDTO;
import ptithcm.edu.pharmacy.dto.ProductDTO;
import ptithcm.edu.pharmacy.dto.PromotionRequest;
import ptithcm.edu.pharmacy.dto.PromotionResponse;
import ptithcm.edu.pharmacy.dto.SimpleBranchDTO; 
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.service.PromotionService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository; // Assuming this exists
    private final ProductRepository productRepository;   // Assuming this exists
    private final BranchRepository branchRepository;     // Assuming this exists

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        log.info("Fetching all promotions");
        return promotionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Integer id) {
        log.info("Fetching promotion by id: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + id));
        return mapToResponse(promotion);
    }

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        log.info("Creating new promotion with code: {}", request.getCode());
        if (request.getCode() != null && promotionRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Promotion code already exists: " + request.getCode());
        }

        Promotion promotion = new Promotion();
        mapToEntity(promotion, request);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        promotion.setTotalUsedCount(0); // Initialize usage count

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion created successfully with id: {}", savedPromotion.getPromotionId());
        return mapToResponse(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Integer id, PromotionRequest request) {
        log.info("Updating promotion with id: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + id));

        // Check if code is being changed and if the new code already exists for another promotion
        if (request.getCode() != null && !request.getCode().equals(promotion.getCode())) {
            promotionRepository.findByCode(request.getCode()).ifPresent(p -> {
                if (!p.getPromotionId().equals(id)) {
                    throw new IllegalArgumentException("Promotion code already exists: " + request.getCode());
                }
            });
        }

        mapToEntity(promotion, request);
        promotion.setUpdatedAt(LocalDateTime.now());

        Promotion updatedPromotion = promotionRepository.save(promotion);
        log.info("Promotion updated successfully with id: {}", updatedPromotion.getPromotionId());
        return mapToResponse(updatedPromotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Integer id) {
        log.info("Deactivating promotion with id: {}", id);
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with id: " + id));
        promotion.setIsActive(false);
        promotion.setUpdatedAt(LocalDateTime.now());
        promotionRepository.save(promotion);
        log.info("Promotion deactivated successfully with id: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getActiveAndApplicablePromotions() {
        log.info("Fetching active and applicable promotions for customers");
        return promotionRepository.findAllByIsActiveTrueAndEndDateAfter(LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void mapToEntity(Promotion promotion, PromotionRequest request) {
        promotion.setCode(request.getCode());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setUsageLimitPerCustomer(request.getUsageLimitPerCustomer());
        promotion.setTotalUsageLimit(request.getTotalUsageLimit());
        promotion.setApplicableScope(request.getApplicableScope() != null ? request.getApplicableScope() : ApplicableScope.ALL);
        promotion.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            promotion.setCategories(categories);
        } else {
            promotion.setCategories(new HashSet<>());
        }

        if (request.getProductIds() != null) {
            Set<Product> products = new HashSet<>(productRepository.findAllById(request.getProductIds()));
            promotion.setProducts(products);
        } else {
            promotion.setProducts(new HashSet<>());
        }
        
        if (request.getBranchIds() != null) {
            Set<Branch> branches = new HashSet<>(branchRepository.findAllById(request.getBranchIds()));
            promotion.setBranches(branches);
        } else {
            promotion.setBranches(new HashSet<>());
        }
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        response.setPromotionId(promotion.getPromotionId());
        response.setCode(promotion.getCode());
        response.setName(promotion.getName());
        response.setDescription(promotion.getDescription());
        response.setDiscountType(promotion.getDiscountType());
        response.setDiscountValue(promotion.getDiscountValue());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        response.setMinOrderValue(promotion.getMinOrderValue());
        response.setUsageLimitPerCustomer(promotion.getUsageLimitPerCustomer());
        response.setTotalUsageLimit(promotion.getTotalUsageLimit());
        response.setTotalUsedCount(promotion.getTotalUsedCount());
        response.setApplicableScope(promotion.getApplicableScope());
        response.setIsActive(promotion.getIsActive());
        response.setCreatedAt(promotion.getCreatedAt());
        response.setUpdatedAt(promotion.getUpdatedAt());

        if (promotion.getCategories() != null) {
            response.setCategories(promotion.getCategories().stream()
                    .map(cat -> new CategoryDTO(cat.getCategoryId(), cat.getName()))
                    .collect(Collectors.toSet()));
        }
        if (promotion.getProducts() != null) {
            response.setProducts(promotion.getProducts().stream()
                    .map(prod -> new ProductDTO(prod.getId(), prod.getName())) // Changed prod.getProductId() to prod.getId()
                    .collect(Collectors.toSet()));
        }
        if (promotion.getBranches() != null) {
            response.setBranches(promotion.getBranches().stream()
                    .map(branch -> new SimpleBranchDTO(branch.getBranchId(), branch.getName())) // This line should now work with the import
                    .collect(Collectors.toSet()));
        }
        return response;
    }
}