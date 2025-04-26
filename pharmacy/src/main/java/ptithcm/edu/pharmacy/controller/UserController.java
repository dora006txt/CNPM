package ptithcm.edu.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ptithcm.edu.pharmacy.dto.UpdateUserRequest;
import ptithcm.edu.pharmacy.dto.UserResponse; // Assuming you have a UserResponse DTO
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    // --- Endpoint: Get Current User Profile ---
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String phoneNumber = authentication.getName(); // Assuming phone number is the username
        User user = userService.getUserByPhoneNumber(phoneNumber);
        if (user == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found in database");
        }

        UserResponse responseDto = mapUserToResponseDto(user);
        log.info("Returning profile for user: {}", phoneNumber);
        return ResponseEntity.ok(responseDto);
    }

    // --- Endpoint: Update Current User Profile ---
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(@RequestBody UpdateUserRequest request) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String phoneNumber = authentication.getName();
        User currentUser = userService.getUserByPhoneNumber(phoneNumber);
         if (currentUser == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found in database");
        }

        log.info("Received request to update profile for user ID: {}", currentUser.getUserId());
        User updatedUser = userService.updateUserProfile(currentUser.getUserId(), request);
        UserResponse responseDto = mapUserToResponseDto(updatedUser);
        log.info("Profile updated successfully for user ID: {}", currentUser.getUserId());
        return ResponseEntity.ok(responseDto);
    }


    // --- Helper mapping function (Create UserResponse DTO if needed) ---
    private UserResponse mapUserToResponseDto(User user) {
        if (user == null) return null;
        // Assuming UserResponse DTO exists with these fields
        return UserResponse.builder()
                .id(user.getUserId())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .isActive(user.getIsActive())
                .lastLogin_login(user.getLastLogin())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet()))
                .build();
    }

    // --- Exception Handler (Optional but Recommended) ---
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason());
        error.put("status", String.valueOf(ex.getStatusCode().value()));
        log.warn("Returning error response: Status {}, Reason: {}", ex.getStatusCode(), ex.getReason());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "An unexpected error occurred.");
        log.error("Unhandled exception in UserController: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}