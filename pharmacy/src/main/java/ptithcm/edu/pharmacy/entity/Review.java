package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false)
    private Byte rating;

    private String comment;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING_APPROVAL;

    @ManyToOne
    @JoinColumn(name = "approved_by_user_id")
    private User approvedByUser;
}