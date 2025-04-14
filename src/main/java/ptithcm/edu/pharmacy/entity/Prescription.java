package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "Prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Integer prescriptionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "clinic_address")
    private String clinicAddress;

    private String diagnosis;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status = PrescriptionStatus.PENDING_VERIFICATION;

    @ManyToOne
    @JoinColumn(name = "verified_by_staff_id")
    private Staff verifiedByStaff;

    @Column(name = "verification_notes")
    private String verificationNotes;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "prescription")
    private Set<PrescriptionItem> prescriptionItems;
}