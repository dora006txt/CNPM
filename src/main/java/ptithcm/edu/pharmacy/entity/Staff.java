package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "title")
    private String title;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "workplace_info")
    private String workplaceInfo;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_available_for_consultation")
    private Boolean isAvailableForConsultation = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}