package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String phoneNumber;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String address; // Add the address field
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin_login;
    private LocalDateTime updatedAt;
    private Set<String> roles;
}