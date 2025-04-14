package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Tìm người dùng theo email
    Optional<User> findByEmail(String email);
    
    // Tìm người dùng theo số điện thoại
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Tìm người dùng đang hoạt động
    List<User> findByIsActiveTrue();

    // Kiểm tra xem email có tồn tại không
    boolean existsByEmail(String email);

    // Kiểm tra xem số điện thoại có tồn tại không
    boolean existsByPhoneNumber(String phoneNumber);

}