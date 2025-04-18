package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.LoginRequest;
import ptithcm.edu.pharmacy.dto.LoginResponse;
import ptithcm.edu.pharmacy.dto.RegisterRequest;
import ptithcm.edu.pharmacy.entity.Role;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.entity.UserRole;
import ptithcm.edu.pharmacy.entity.UserRoleId;
import ptithcm.edu.pharmacy.repository.RoleRepository;
import ptithcm.edu.pharmacy.repository.UserRepository;
import ptithcm.edu.pharmacy.repository.UserRoleRepository;
import ptithcm.edu.pharmacy.security.JwtUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate the user using phone number
        Authentication authentication = authenticationManager.authenticate(
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
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Assign default CUSTOMER role to the user
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
}