package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Branch;


@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    // You can add custom query methods here if needed, e.g.,
    // List<Branch> findByIsActiveTrue();
}