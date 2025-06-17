package ptithcm.edu.pharmacy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ptithcm.edu.pharmacy.dto.LoginRequest;
import ptithcm.edu.pharmacy.dto.LoginResponse;
import ptithcm.edu.pharmacy.dto.RegisterRequest;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.service.UserService; // Assuming UserService exists

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Or your preferred base path for auth
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService; // Inject UserService

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

    // --- New Endpoint: Forgot Password ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
        }

        log.info("Received forgot password request for email: {}", email);

        try {
            boolean result = userService.processForgotPassword(email.trim());
            // Always return a success-like response to prevent email enumeration
            // The actual email sending happens asynchronously if configured
            log.info("Forgot password process completed for email: {}. User found: {}", email, result);
            return ResponseEntity.ok(
                    Map.of("message", "If an account with that email exists, a password reset email has been sent."));
        } catch (Exception e) {
            log.error("Unexpected error during forgot password process for email {}: {}", email, e.getMessage(), e);
            // Return a generic error in case of unexpected issues
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An unexpected error occurred. Please try again later."));
        }
    }
    // --- End Forgot Password ---

    // --- New Endpoint: Change Password ---
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }

        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");

        // Validate input
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Current password is required"));
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password is required"));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password and confirm password do not match"));
        }

        // Validate password strength - changed from 8 to 6 characters
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters long"));
        }

        try {
            // Get the authenticated user's phone number from the Principal
            String phoneNumber = principal.getName();

            // Get the user from the database
            User user = userService.getUserByPhoneNumber(phoneNumber);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
            }

            // Change the password for the authenticated user
            userService.changePassword(user.getUserId(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    // --- End Change Password Endpoint ---
}