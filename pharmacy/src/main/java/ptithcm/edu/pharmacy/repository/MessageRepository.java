package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.Message;

import java.util.List; // Thêm import này

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Thêm phương thức này để lấy lịch sử tin nhắn theo ID của ConsultationRequest
    List<Message> findByConsultationRequest_RequestIdOrderBySentAtAsc(Integer consultationRequestId);
}