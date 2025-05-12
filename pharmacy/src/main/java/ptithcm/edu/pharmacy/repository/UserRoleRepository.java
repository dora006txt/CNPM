package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.User; // Import User entity
import ptithcm.edu.pharmacy.entity.UserRole;
import ptithcm.edu.pharmacy.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    void deleteByUser(User user); // Add this method
}