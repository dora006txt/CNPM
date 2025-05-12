package ptithcm.edu.pharmacy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ptithcm.edu.pharmacy.dto.UpdateUserRequest;
import ptithcm.edu.pharmacy.dto.UserResponse;
import ptithcm.edu.pharmacy.service.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List; // Import List

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    // --- Endpoint: Get Current User Profile ---
    @GetMapping("/users/me") // Path changed to /api/users/me
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("Attempt to access /users/me without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String currentUsername = authentication.getName(); // This is typically the phone number
        log.info("Fetching profile for current user: {}", currentUsername);

        try {
            // Assuming username is the phone number used for login
            UserResponse userResponse = userService.getUserProfileByPhoneNumber(currentUsername);
            return ResponseEntity.ok(userResponse);
        } catch (ResponseStatusException e) {
            log.error("Error fetching profile for user {}: {}", currentUsername, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            log.error("Unexpected error fetching profile for user {}: {}", currentUsername, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Endpoint: Update Current User Profile ---
    @PutMapping("/users/me") // Path changed to /api/users/me
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@RequestBody UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("Attempt to update profile /users/me without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String currentUsername = authentication.getName(); // This is typically the phone number
        log.info("Updating profile for current user: {}", currentUsername);

        try {
            UserResponse updatedUser = userService.updateUserProfileByPhoneNumber(currentUsername, updateUserRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException e) {
            log.error("Error updating profile for user {}: {}", currentUsername, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null); // Or a custom error response
        } catch (Exception e) {
            log.error("Unexpected error updating profile for user {}: {}", currentUsername, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Admin Endpoints for User Management ---

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ADMIN')") // Add this line
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Admin request: Get all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Add this line
    public ResponseEntity<UserResponse> getUserByIdAsAdmin(@PathVariable Integer id) {
        log.info("Admin request: Get user by ID: {}", id);
        try {
            UserResponse user = userService.getUserByIdAsAdmin(id);
            return ResponseEntity.ok(user);
        } catch (ResponseStatusException e) {
            log.warn("Admin request: User not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Add this line
    public ResponseEntity<Void> deleteUserAsAdmin(@PathVariable Integer id) {
        log.info("Admin request: Delete (deactivate) user with ID: {}", id);
        try {
            userService.deleteUserAsAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            log.warn("Admin request: Failed to delete user ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}