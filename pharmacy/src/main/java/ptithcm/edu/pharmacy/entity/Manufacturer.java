package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data; // Or add getters/setters manually

@Entity
@Table(name = "Manufacturers")
@Data // Lombok annotation for getters, setters, etc.
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY) // Many manufacturers can be from one country
    @JoinColumn(name = "country_id", nullable = false) // Foreign key column in manufacturers table
    private Country country;

    // Constructors, Getters, Setters (generated by @Data or add manually)
    // Note: Be careful with Lombok's @ToString if relationships are bidirectional to avoid stack overflow.
    // Consider excluding related fields from toString or implementing it manually if needed.
}