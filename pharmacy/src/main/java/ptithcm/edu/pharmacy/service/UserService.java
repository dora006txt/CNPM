package ptithcm.edu.pharmacy.service;

import org.apache.commons.lang3.RandomStringUtils; // For random password generation
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
import ptithcm.edu.pharmacy.entity.Role;
import ptithcm.edu.pharmacy.entity.User; // Assuming User entity exists
import ptithcm.edu.pharmacy.entity.UserRole;
import ptithcm.edu.pharmacy.entity.UserRoleId;
import ptithcm.edu.pharmacy.repository.UserRepository; // Assuming UserRepository exists
import ptithcm.edu.pharmacy.repository.RoleRepository; // Import RoleRepository
import ptithcm.edu.pharmacy.repository.UserRoleRepository; // Import UserRoleRepository
import ptithcm.edu.pharmacy.security.JwtUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
                        loginRequest.getPassword()
                )
        );

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
    // --- New Method: Forgot Password ---
    @Transactional
    public boolean processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // --- Replace deprecated method ---
            // Generate a temporary password (e.g., 10 alphanumeric characters)
            // String temporaryPassword = RandomStringUtils.randomAlphanumeric(10); // Deprecated
            String temporaryPassword = RandomStringUtils.random(10, true, true); // Generate alphanumeric password using non-deprecated method
            // --- End replacement ---
            
            // Add more visible console output for the new password
            System.out.println("============================================================");
            System.out.println("NEW PASSWORD GENERATED for user: " + email);
            System.out.println("TEMPORARY PASSWORD: " + temporaryPassword);
            System.out.println("============================================================");
            
            log.info("Generated temporary password for user {}", email); // Log temporarily for debugging if needed, remove in prod

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
    
    // Add this method to get user by phone number
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);
    }
}