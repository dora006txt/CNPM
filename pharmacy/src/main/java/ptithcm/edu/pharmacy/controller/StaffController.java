package ptithcm.edu.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.StaffRequest;
import ptithcm.edu.pharmacy.dto.StaffResponse;
import ptithcm.edu.pharmacy.service.StaffService;

import java.util.List;

@RestController
// Keep existing admin path for admin operations
// @RequestMapping("/api/admin/staff") // Base path for admin staff management
@RequiredArgsConstructor
// @PreAuthorize("hasAuthority('ADMIN')") // Secure all endpoints in this controller for ADMIN
public class StaffController {

    private final StaffService staffService;

    // --- Public Doctor Endpoints ---
    @GetMapping("/api/public/doctors")
    public ResponseEntity<List<StaffResponse>> getAvailableDoctors() {
        List<StaffResponse> doctors = staffService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/api/public/doctors/{id}")
    public ResponseEntity<StaffResponse> getDoctorById(@PathVariable Integer id) {
        // This uses the existing getStaffById, which should be fine for public view
        // if the StaffResponse DTO is suitable for public consumption.
        StaffResponse doctor = staffService.getStaffById(id);
        return ResponseEntity.ok(doctor);
    }

    // --- Admin Staff Management Endpoints ---

    // GET /api/admin/staff - Get all staff members
    @GetMapping("/api/admin/staff")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        List<StaffResponse> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    // GET /api/admin/staff/{id} - Get specific staff member by ID
    @GetMapping("/api/admin/staff/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StaffResponse> getAdminStaffById(@PathVariable Integer id) {
        StaffResponse staff = staffService.getStaffById(id);
        return ResponseEntity.ok(staff);
    }

    // POST /api/admin/staff - Create a new staff member
    @PostMapping("/api/admin/staff")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StaffResponse> createStaff(@RequestBody StaffRequest staffRequest) {
        StaffResponse createdStaff = staffService.createStaff(staffRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
    }

    // PUT /api/admin/staff/{id} - Update an existing staff member
    @PutMapping("/api/admin/staff/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StaffResponse> updateStaff(@PathVariable Integer id, @RequestBody StaffRequest staffRequest) {
        StaffResponse updatedStaff = staffService.updateStaff(id, staffRequest);
        return ResponseEntity.ok(updatedStaff);
    }

    // DELETE /api/admin/staff/{id} - Deactivate a staff member
    @DeleteMapping("/api/admin/staff/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}