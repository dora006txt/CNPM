package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ptithcm.edu.pharmacy.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
}