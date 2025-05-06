package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class StaffRequest {
    // User details (for creating a new user or linking to existing)
    private String phoneNumber; // Required for new user
    private String password;    // Required for new user
    private String email;       // Optional for new user
    private Integer userId;     // Optional: Link to existing user ID

    // Staff details
    private Integer branchId; // Required: The branch this staff manages/belongs to
    private String fullName;  // Required
    private String title;     // e.g., "Branch Manager"
    private String specialty;
    private String workplaceInfo;
    private String profileImageUrl;
    private Boolean isAvailableForConsultation;
    private Boolean isActive; // For activating/deactivating staff
}