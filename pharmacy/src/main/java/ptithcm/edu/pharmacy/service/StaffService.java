package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.StaffRequest;
import ptithcm.edu.pharmacy.dto.StaffResponse;

import java.util.List;

public interface StaffService {
    List<StaffResponse> getAllStaff();
    StaffResponse getStaffById(Integer id);
    StaffResponse createStaff(StaffRequest request);
    StaffResponse updateStaff(Integer id, StaffRequest request);
    void deleteStaff(Integer id); // Deactivates staff
}