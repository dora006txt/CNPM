package ptithcm.edu.pharmacy.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StaffResponse {
    private Integer staffId;
    private Integer userId; // ID of the linked user account
    private String userPhoneNumber; // Phone number from linked user
    private String userEmail; // Email from linked user
    private Integer branchId; // ID of the primary branch
    private String branchName; // Name of the primary branch
    private String fullName;
    private String title;
    private String specialty;
    private String workplaceInfo;
    private String profileImageUrl;
    private Boolean isAvailableForConsultation;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}