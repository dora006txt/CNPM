package ptithcm.edu.pharmacy.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}