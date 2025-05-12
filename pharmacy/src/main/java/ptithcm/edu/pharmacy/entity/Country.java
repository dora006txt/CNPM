package ptithcm.edu.pharmacy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Add this import
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "Countries") // Assuming your table name is Countries
@Data
@NoArgsConstructor
@AllArgsConstructor
// Add this annotation to ignore Hibernate specific properties during serialization
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id") // Assuming this is your primary key column name
    private Integer countryId;

    @Column(name = "country_name", nullable = false, unique = true) // Assuming column name and constraints
    private String countryName;

    @Column(name = "country_code", unique = true) // Assuming column name and constraints
    private String countryCode;

    // If Country has a relationship with Manufacturer (e.g., OneToMany)
    // This is just an example, adjust based on your actual entity relationships
    // If you have such a relationship, you might also need @JsonIgnore or DTOs
    // to prevent circular dependencies or further lazy loading issues.
    /*
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @JsonIgnore // Example: to break potential circular dependencies if Manufacturer also refers back
    private Set<Manufacturer> manufacturers;
    */

    // Getters and setters
    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}