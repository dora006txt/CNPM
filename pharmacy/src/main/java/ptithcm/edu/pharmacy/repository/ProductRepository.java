package ptithcm.edu.pharmacy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ptithcm.edu.pharmacy.entity.Product;
import ptithcm.edu.pharmacy.entity.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySkuIgnoreCase(String sku);

    Optional<Product> findByNameIgnoreCase(String name);

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySlugIgnoreCase(String slug);

    Page<Product> findByStatusOrderByAverageRatingDesc(ProductStatus status, Pageable pageable);

    Optional<Product> findBySlug(String slug);

    List<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryIdParam")
    List<Product> findByCategoryId(@Param("categoryIdParam") Integer categoryId);

    List<Product> findByBrandId(Integer brandId);

    @Query(value = "SELECT p.id, SUM(oi.quantity) " +
            "FROM Product p JOIN p.orderItems oi " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantity) DESC", countQuery = "SELECT COUNT(DISTINCT p.id) " +
                    "FROM Product p JOIN p.orderItems oi")
    List<Object[]> findTopSellingProductIdsAndQuantities(Pageable pageable);
}