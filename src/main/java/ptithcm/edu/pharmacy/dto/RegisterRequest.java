package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String password;
    private String fullName;
    private String email;
}