package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.ConsultationRequestDTO; // Bạn cần tạo DTO này
import ptithcm.edu.pharmacy.entity.ConsultationRequest;
import ptithcm.edu.pharmacy.service.ConsultationRequestService; // Bạn cần tạo Service này

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.UserRepository;

@RestController
@RequestMapping("/api/consultation-requests")
public class ConsultationRequestController {

    @Autowired
    private ConsultationRequestService consultationRequestService;

    @Autowired
    private UserRepository userRepository; // Thêm UserRepository

    @PostMapping
    public ResponseEntity<?> createConsultationRequest(
            @RequestBody ConsultationRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            User currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

            ConsultationRequest newRequest = consultationRequestService.createRequest(currentUser.getUserId(), requestDTO);
            return ResponseEntity.ok(newRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request data: " + e.getMessage());
        } catch (RuntimeException e) { // Bắt các lỗi runtime khác, ví dụ User/Branch not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating consultation request: " + e.getMessage());
        }
    }
}