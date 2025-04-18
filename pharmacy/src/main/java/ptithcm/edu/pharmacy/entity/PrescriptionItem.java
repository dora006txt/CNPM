package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Prescription_Items")
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_item_id")
    private Integer prescriptionItemId;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "product_name_on_rx", nullable = false)
    private String productNameOnRx;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String dosage;
    private String frequency;
    private String duration;

    @Column(name = "quantity_prescribed")
    private String quantityPrescribed;

    private String notes;
}