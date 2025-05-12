package ptithcm.edu.pharmacy.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String password;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private Boolean isActive; // For admin to set during creation
    private Set<String> roles;  // For admin to set roles during creation

    // Constructors (if needed, Lombok can also generate @AllArgsConstructor, @NoArgsConstructor, etc.)
}