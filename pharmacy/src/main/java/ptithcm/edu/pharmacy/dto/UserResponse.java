package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // Add this
import lombok.AllArgsConstructor; // Add this
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor // Ensures new UserResponse() works
@AllArgsConstructor // Good practice to have if you have @Builder and @NoArgsConstructor
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