package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String address; // Add the address field
    // Note: Password changes should have a dedicated endpoint.
}