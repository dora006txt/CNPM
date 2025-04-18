package ptithcm.edu.pharmacy.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private Integer userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private List<String> roles;
    private String token;
}