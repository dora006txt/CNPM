package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.Set; // Import Set

@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private Boolean isActive; // Add this field
    private Set<String> roles; // Add this field
    private String password; // Add this field for admin password resets
    // Note: Password changes should ideally have a dedicated endpoint for users,
    // but for an admin update, including it here can be acceptable.
}