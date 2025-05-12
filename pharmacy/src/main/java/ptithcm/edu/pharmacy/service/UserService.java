package ptithcm.edu.pharmacy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; // Assuming Spring Security
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ptithcm.edu.pharmacy.dto.LoginRequest;
import ptithcm.edu.pharmacy.dto.LoginResponse;
import ptithcm.edu.pharmacy.dto.RegisterRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import ptithcm.edu.pharmacy.dto.UpdateUserRequest;
import ptithcm.edu.pharmacy.dto.UserResponse;
import ptithcm.edu.pharmacy.entity.Role;
import java.util.stream.Collectors; // Import Collectors
import ptithcm.edu.pharmacy.entity.User; // Assuming User entity exists
import ptithcm.edu.pharmacy.entity.UserRole;
import ptithcm.edu.pharmacy.entity.UserRoleId;
import ptithcm.edu.pharmacy.repository.UserRepository; // Assuming UserRepository exists
import ptithcm.edu.pharmacy.repository.RoleRepository; // Import RoleRepository
import ptithcm.edu.pharmacy.repository.UserRoleRepository; // Import UserRoleRepository
import ptithcm.edu.pharmacy.security.JwtUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List; // Import List
import java.util.Optional;
import java.security.SecureRandom; // Import SecureRandom

@Service
public class UserService { // Or your relevant service name

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // --- Add injections for the missing repositories ---
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    // --- End injections ---

    @Autowired
    private EmailService emailService; // Inject EmailService

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate the user using phone number
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhoneNumber(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Find the user
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Create response
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getRoleName())
                .toList());
        response.setToken(jwt);

        return response;
    }

    public User register(RegisterRequest registerRequest) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
        }

        // Check if email already exists (if provided)
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty()
                && userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        // --- Ensure you are setting the correct field (e.g., passwordHash) ---
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        // --- End field check ---
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);

        // Assign default CUSTOMER role to the user
        // Now roleRepository and userRoleRepository can be resolved
        Optional<Role> customerRole = roleRepository.findByRoleName("CUSTOMER");
        if (customerRole.isPresent()) {
            UserRole userRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(savedUser.getUserId());
            userRoleId.setRoleId(customerRole.get().getRoleId());

            userRole.setId(userRoleId);
            userRole.setUser(savedUser);
            userRole.setRole(customerRole.get());

            userRoleRepository.save(userRole);
        } else {
            // If CUSTOMER role doesn't exist, create it
            Role newCustomerRole = new Role();
            newCustomerRole.setRoleName("CUSTOMER");
            newCustomerRole.setDescription("Regular customer role");
            Role savedRole = roleRepository.save(newCustomerRole);

            UserRole userRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(savedUser.getUserId());
            userRoleId.setRoleId(savedRole.getRoleId());

            userRole.setId(userRoleId);
            userRole.setUser(savedUser);
            userRole.setRole(savedRole);

            userRoleRepository.save(userRole);
        }

        return savedUser;
    }

    // Method to get user profile by phone number (for authenticated user)
    @Transactional(readOnly = true)
    public UserResponse getUserProfileByPhoneNumber(String phoneNumber) {
        log.info("Fetching profile for phone number: {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("User not found with phone number: {}", phoneNumber);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found.");
                });
        return mapToUserResponse(user);
    }

    // Method to update user profile by phone number (for authenticated user)
    @Transactional
    public UserResponse updateUserProfileByPhoneNumber(String phoneNumber, UpdateUserRequest request) {
        log.info("Updating profile for phone number: {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("User not found with phone number: {}", phoneNumber);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found.");
                });

        // Update allowed fields
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Check if the new email is different from the current one and if it's already
            // used by another user
            if (user.getEmail() == null || !request.getEmail().equalsIgnoreCase(user.getEmail())) {
                userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                    if (!existingUser.getUserId().equals(user.getUserId())) {
                        log.warn("Attempt to update email to {}, which is already in use by another user.",
                                request.getEmail());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Email address is already in use by another account.");
                    }
                });
            }
            user.setEmail(request.getEmail());
        } else if (request.getEmail() != null && request.getEmail().isBlank()) { // Allow clearing email
            user.setEmail(null);
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getGender() != null) { // Gender can be set or cleared
            user.setGender(request.getGender().isBlank() ? null : request.getGender());
        }

        if (request.getAddress() != null) { // Address can be set or cleared
            user.setAddress(request.getAddress().isBlank() ? null : request.getAddress());
        }

        // Password changes should be handled by a dedicated changePassword endpoint
        // Roles and isActive status should not be updatable by the user themselves here

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", updatedUser.getUserId());
        return mapToUserResponse(updatedUser);
    }

    // --- New Method: Forgot Password ---
    @Transactional
    public boolean processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // --- Replace deprecated method ---
            // Generate a temporary password (e.g., 10 alphanumeric characters)
            String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lower = upper.toLowerCase();
            String digits = "0123456789";
            String alphanum = upper + lower + digits;
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                sb.append(alphanum.charAt(random.nextInt(alphanum.length())));
            }
            String temporaryPassword = sb.toString();
            // --- End replacement ---

            // Add more visible console output for the new password
            System.out.println("============================================================");
            System.out.println("NEW PASSWORD GENERATED for user: " + email);
            System.out.println("TEMPORARY PASSWORD: " + temporaryPassword);
            System.out.println("============================================================");

            log.info("Generated temporary password for user {}", email); // Log temporarily for debugging if needed,
                                                                         // remove in prod

            // Encode the temporary password before saving
            // --- Ensure you are setting the correct field (e.g., passwordHash) ---
            user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
            // --- End field check ---
            userRepository.save(user); // Update user with new hashed password

            // Send the temporary password via email
            String emailSubject = "Your Password Reset Request";
            String emailText = "Hello " + user.getFullName() + ",\n\n" // Assuming user has getFullName()
                    + "Your temporary password is: " + temporaryPassword + "\n\n"
                    + "Please log in using this temporary password and change it immediately for security reasons.\n\n"
                    + "Regards,\nYour Pharmacy Application";

            emailService.sendPasswordResetEmail(user.getEmail(), emailSubject, emailText);

            return true; // Indicate success
        } else {
            log.warn("Forgot password request for non-existent email: {}", email);
            // Do not throw an error here to prevent email enumeration attacks
            return false; // Indicate user not found (or handle silently)
        }
    }
    // --- End Forgot Password ---

    // --- Change Password Method ---
    @Transactional
    public boolean changePassword(Integer userId, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new RuntimeException("Current password is incorrect");
            }

            // Encode and set new password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Password changed successfully for user ID: {}", userId);
            return true;
        } else {
            log.warn("Password change attempt for non-existent user ID: {}", userId);
            throw new RuntimeException("User not found");
        }
    }
    // --- End Change Password Method ---

    // Add this method to get user by phone number (if not already present)
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElse(null); // Or throw an exception if preferred
    }

    // --- New Method: Update User Profile ---
    @Transactional
    public User updateUserProfile(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        // Update fields from request if they are provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            // Add email uniqueness check
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        // Add the address update logic
        if (request.getAddress() != null) { // Check if address is provided in the request
            user.setAddress(request.getAddress());
        }

        user.setUpdatedAt(LocalDateTime.now()); // Update timestamp

        return userRepository.save(user);
    }
    // --- End Update User Profile ---

    // --- Helper Method: Map User to UserResponse DTO ---
    // Consider making this public or moving it to a dedicated mapper class if used
    // elsewhere
    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        response.setId(user.getUserId());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setGender(user.getGender());
        response.setAddress(user.getAddress());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        // Verify the setter name below against your UserResponse DTO.
        // If the field is 'lastLogin', it should be
        // 'response.setLastLogin(user.getLastLogin());'
        response.setLastLogin_login(user.getLastLogin());
        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toSet()));
        } else {
            response.setRoles(new HashSet<>());
        }
        return response;
    }
    // --- End Helper Method ---

    // --- Admin: Get All Users ---
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Admin: Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse) // Changed mapUserToResponse to mapToUserResponse
                .collect(Collectors.toList());
    }

    // --- Admin: Get User By ID ---
    @Transactional(readOnly = true)
    public UserResponse getUserByIdAsAdmin(Integer userId) {
        log.info("Admin: Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        return mapToUserResponse(user); // Changed mapUserToResponse to mapToUserResponse
    }


    // --- Admin: Delete (Deactivate) User ---
    @Transactional
    public void deleteUserAsAdmin(Integer userId) {
        log.info("Admin: Deactivating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        if (!user.getIsActive()) {
            log.warn("Admin: User with ID {} is already inactive.", userId);
            // Optionally, throw an exception or just return
            // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already
            // inactive.");
            return; // Or proceed to fully delete if that's the requirement
        }

        user.setIsActive(false); // Deactivate the user
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Admin: User deactivated successfully with ID: {}", userId);
        // If actual deletion is required: userRepository.delete(user);
    }

}