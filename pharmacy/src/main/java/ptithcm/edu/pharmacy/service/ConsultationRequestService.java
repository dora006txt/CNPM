package ptithcm.edu.pharmacy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.ConsultationRequestDTO;
import ptithcm.edu.pharmacy.entity.ConsultationRequest;
import ptithcm.edu.pharmacy.entity.ConsultationRequestStatus;
import ptithcm.edu.pharmacy.repository.ConsultationRequestRepository;
import ptithcm.edu.pharmacy.repository.UserRepository;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.entity.Branch;
import ptithcm.edu.pharmacy.repository.BranchRepository;
import ptithcm.edu.pharmacy.entity.RequestType;
import ptithcm.edu.pharmacy.entity.Staff; // Thêm import Staff
import ptithcm.edu.pharmacy.repository.StaffRepository; // Thêm import StaffRepository
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List; // Thêm import List
import java.util.Random; // Thêm import Random

@Service
public class ConsultationRequestService {

    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private StaffRepository staffRepository; // Inject StaffRepository

    public ConsultationRequest createRequest(Integer userId, ConsultationRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        ConsultationRequest newRequest = new ConsultationRequest();
        newRequest.setUser(user);
        newRequest.setUserMessage(dto.getUserMessage());

        // Chuyển đổi String requestType từ DTO sang enum RequestType
        try {
            if (dto.getRequestType() == null || dto.getRequestType().trim().isEmpty()) {
                throw new IllegalArgumentException("Request type cannot be empty.");
            }
            newRequest.setRequestType(RequestType.valueOf(dto.getRequestType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Xử lý trường hợp giá trị requestType không hợp lệ hoặc null/empty
            throw new IllegalArgumentException("Invalid or missing request type: " + dto.getRequestType()
                    + ". Valid types are: " + java.util.Arrays.toString(RequestType.values()));
        }

        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + dto.getBranchId()));
            newRequest.setBranch(branch);
        }

        newRequest.setRequestTime(LocalDateTime.now());
        newRequest.setLastUpdated(LocalDateTime.now());
        newRequest.setStatus(ConsultationRequestStatus.pending);

        // Tìm và gán nhân viên tư vấn ngẫu nhiên
        List<Staff> availableStaff = staffRepository.findByIsActiveTrueAndIsAvailableForConsultationTrue();
        if (!availableStaff.isEmpty()) {
            Random random = new Random();
            Staff randomStaff = availableStaff.get(random.nextInt(availableStaff.size()));
            newRequest.setAssignedStaff(randomStaff); 
            // Optional: Cập nhật trạng thái của request nếu cần
            // newRequest.setStatus(ConsultationRequestStatus.ASSIGNED);
            System.out.println("Assigned staff " + randomStaff.getStaffId() + " to consultation request "
                    + newRequest.getRequestId());
        } else {
            // Xử lý trường hợp không có nhân viên nào sẵn sàng
            System.err.println("Warning: No staff available for consultation request from user " + userId
                    + ". Request ID will be created without assigned staff.");
            // Bạn có thể quyết định ném lỗi, hoặc để assignedStaff là null và xử lý ở tầng
            // controller/client
        }

        return consultationRequestRepository.save(newRequest);
    }
}