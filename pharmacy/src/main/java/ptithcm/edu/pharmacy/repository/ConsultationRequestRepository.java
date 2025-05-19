package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.ConsultationRequest;

public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Integer> {
}