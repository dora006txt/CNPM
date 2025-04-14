package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ptithcm.edu.pharmacy.dto.LoginRequest;
import ptithcm.edu.pharmacy.dto.LoginResponse;
import ptithcm.edu.pharmacy.dto.RegisterRequest;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(registerRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("message", "User registered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}