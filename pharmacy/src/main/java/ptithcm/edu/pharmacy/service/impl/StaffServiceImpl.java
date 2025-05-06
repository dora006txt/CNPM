package ptithcm.edu.pharmacy.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ptithcm.edu.pharmacy.dto.StaffRequest;
import ptithcm.edu.pharmacy.dto.StaffResponse;
import ptithcm.edu.pharmacy.entity.*;
import ptithcm.edu.pharmacy.repository.*;
import ptithcm.edu.pharmacy.service.StaffService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        log.info("Fetching all staff members");
        return staffRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // New method to get available doctors
    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAvailableDoctors() {
        log.info("Fetching all available doctors (active and available for consultation)");
        return staffRepository.findAll().stream()
                .filter(staff -> staff.getIsActive() && staff.getIsAvailableForConsultation()) // Key filter
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Integer id) {
        log.info("Fetching staff member with ID: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Staff not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with ID: " + id);
                });
        return mapToResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        log.info("Attempting to create new staff for branch ID: {}", request.getBranchId());

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch not found with ID: " + request.getBranchId()));

        User user;
        // Check if linking to existing user or creating new
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found with ID: " + request.getUserId()));
            // Check if this user is already linked to another staff profile
            if (staffRepository.existsByUser_UserId(user.getUserId())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID " + user.getUserId() + " is already associated with a staff profile.");
            }
            log.info("Linking new staff profile to existing user ID: {}", user.getUserId());
        } else {
            // Create a new user
            if (request.getPhoneNumber() == null || request.getPassword() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number and password are required to create a new user for staff.");
            }
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number " + request.getPhoneNumber() + " is already in use.");
            }
            if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + request.getEmail() + " is already in use.");
            }

            user = new User();
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName()); // Use staff full name for user full name initially
            user.setEmail(request.getEmail());
            user.setIsActive(true); // Activate user by default
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
            log.info("Created new user with ID: {} for staff", user.getUserId());

            // Assign STAFF role (or MANAGER if you have one)
            assignRoleToUser(user, "STAFF"); // Assuming a 'STAFF' role exists
        }

        // Create Staff entity
        Staff staff = new Staff();
        staff.setUser(user);
        staff.setBranch(branch);
        staff.setFullName(request.getFullName());
        staff.setTitle(request.getTitle() != null ? request.getTitle() : "Staff"); // Default title
        staff.setSpecialty(request.getSpecialty());
        staff.setWorkplaceInfo(request.getWorkplaceInfo());
        staff.setProfileImageUrl(request.getProfileImageUrl());
        staff.setIsAvailableForConsultation(request.getIsAvailableForConsultation() != null ? request.getIsAvailableForConsultation() : true);
        staff.setIsActive(request.getIsActive() != null ? request.getIsActive() : true); // Default to active
        staff.setCreatedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());

        Staff savedStaff = staffRepository.save(staff);
        log.info("Successfully created staff with ID: {}", savedStaff.getStaffId());
        return mapToResponse(savedStaff);
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Integer id, StaffRequest request) {
        log.info("Attempting to update staff with ID: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with ID: " + id));

        // Update branch if changed
        if (request.getBranchId() != null && !staff.getBranch().getBranchId().equals(request.getBranchId())) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch not found with ID: " + request.getBranchId()));
            staff.setBranch(branch);
            log.info("Updated branch for staff ID: {} to branch ID: {}", id, branch.getBranchId());
        }

        // Update staff details
        if (request.getFullName() != null) staff.setFullName(request.getFullName());
        if (request.getTitle() != null) staff.setTitle(request.getTitle());
        if (request.getSpecialty() != null) staff.setSpecialty(request.getSpecialty());
        if (request.getWorkplaceInfo() != null) staff.setWorkplaceInfo(request.getWorkplaceInfo());
        if (request.getProfileImageUrl() != null) staff.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getIsAvailableForConsultation() != null) staff.setIsAvailableForConsultation(request.getIsAvailableForConsultation());
        if (request.getIsActive() != null) staff.setIsActive(request.getIsActive());

        staff.setUpdatedAt(LocalDateTime.now());

        Staff updatedStaff = staffRepository.save(staff);
        log.info("Successfully updated staff with ID: {}", updatedStaff.getStaffId());
        return mapToResponse(updatedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(Integer id) {
        log.info("Attempting to deactivate staff with ID: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with ID: " + id));

        if (!staff.getIsActive()) {
            log.warn("Staff with ID: {} is already inactive.", id);
            // Optionally throw an exception or just return
            // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff is already inactive.");
            return;
        }

        staff.setIsActive(false);
        staff.setUpdatedAt(LocalDateTime.now());
        staffRepository.save(staff);
        log.info("Successfully deactivated staff with ID: {}", id);

        // Optionally deactivate the associated user account as well
        // User user = staff.getUser();
        // if (user != null && user.getIsActive()) {
        //     user.setIsActive(false);
        //     user.setUpdatedAt(LocalDateTime.now());
        //     userRepository.save(user);
        //     log.info("Deactivated associated user account with ID: {}", user.getUserId());
        // }
    }

    // Helper to assign a role
    private void assignRoleToUser(User user, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    log.warn("Role '{}' not found, creating it.", roleName);
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    newRole.setDescription(roleName + " role"); // Basic description
                    return roleRepository.save(newRole);
                });

        // Check if user already has the role
        boolean alreadyHasRole = user.getRoles() != null && user.getRoles().stream().anyMatch(r -> r.getRoleId().equals(role.getRoleId()));
        if (!alreadyHasRole) {
            UserRole userRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId(user.getUserId(), role.getRoleId());
            userRole.setId(userRoleId);
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
            log.info("Assigned role '{}' to user ID: {}", roleName, user.getUserId());
        } else {
             log.info("User ID: {} already has role '{}'", user.getUserId(), roleName);
        }
    }


    // Helper to map Staff entity to StaffResponse DTO
    private StaffResponse mapToResponse(Staff staff) {
        User user = staff.getUser(); // Assumes User is eagerly fetched or session is open
        Branch branch = staff.getBranch(); // Assumes Branch is eagerly fetched or session is open

        return StaffResponse.builder()
                .staffId(staff.getStaffId())
                .userId(user != null ? user.getUserId() : null)
                .userPhoneNumber(user != null ? user.getPhoneNumber() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .branchId(branch != null ? branch.getBranchId() : null)
                .branchName(branch != null ? branch.getName() : null) // Assuming Branch has getName()
                .fullName(staff.getFullName())
                .title(staff.getTitle())
                .specialty(staff.getSpecialty())
                .workplaceInfo(staff.getWorkplaceInfo())
                .profileImageUrl(staff.getProfileImageUrl())
                .isAvailableForConsultation(staff.getIsAvailableForConsultation())
                .isActive(staff.getIsActive())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }
}