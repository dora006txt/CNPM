package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.PaymentType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
    // Find all active payment types
    List<PaymentType> findByIsActiveTrue();

    // Find by type name (useful for checking duplicates before adding)
    Optional<PaymentType> findByTypeNameIgnoreCase(String typeName);
}