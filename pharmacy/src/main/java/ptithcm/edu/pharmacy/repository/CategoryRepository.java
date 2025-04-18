package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Check if a category with the given name exists
    boolean existsByName(String name);

    // Check if a category with the given slug exists
    boolean existsBySlug(String slug);

    // Find a category by its slug (optional, but useful)
    Optional<Category> findBySlug(String slug);
}