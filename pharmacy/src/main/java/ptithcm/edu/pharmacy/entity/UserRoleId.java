package ptithcm.edu.pharmacy.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor; // Import AllArgsConstructor
import lombok.Data;
import lombok.NoArgsConstructor; // Import NoArgsConstructor for JPA
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor // Add NoArgsConstructor for JPA requirements
@AllArgsConstructor // Add AllArgsConstructor to ensure the constructor is generated
public class UserRoleId implements Serializable {
    private Integer userId;
    private Integer roleId;
}