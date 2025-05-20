package ptithcm.edu.pharmacy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import ptithcm.edu.pharmacy.dto.ChatMessageDTO;
import ptithcm.edu.pharmacy.dto.MessageDTO; // Thêm import này
import ptithcm.edu.pharmacy.entity.ConsultationRequest;
import ptithcm.edu.pharmacy.entity.Message;
import ptithcm.edu.pharmacy.entity.User;
import ptithcm.edu.pharmacy.repository.ConsultationRequestRepository;
import ptithcm.edu.pharmacy.repository.UserRepository;
import ptithcm.edu.pharmacy.service.MessageService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;

    @MessageMapping("/chat.sendMessage/{consultationRequestId}")
    public void sendMessage(@DestinationVariable Integer consultationRequestId,
            @Payload ChatMessageDTO chatMessageDTO,
            Principal principal) {

        if (principal == null) {
            logger.error("sendMessage: User not authenticated for sending message to consultation: {}",
                    consultationRequestId);
            return;
        }
        logger.info("sendMessage: Authenticated user principal name: {}", principal.getName());

        // Giả định principal.getName() trả về số điện thoại hoặc một định danh duy nhất
        // mà findByPhoneNumber có thể sử dụng.
        // Nếu principal.getName() trả về một thứ khác (ví dụ: ID người dùng dưới dạng
        // chuỗi), bạn cần thay đổi phương thức truy vấn cho phù hợp.
        User sender = userRepository.findByPhoneNumber(principal.getName())
                .orElse(null);

        if (sender == null) {
            logger.error(
                    "sendMessage: Sender not found for principal name: {}. Ensure principal.getName() is the correct identifier (e.g., phone number) and the user exists.",
                    principal.getName());
            return;
        }

        ConsultationRequest consultationRequest = consultationRequestRepository.findById(consultationRequestId)
                .orElse(null);

        if (consultationRequest == null) {
            logger.error("sendMessage: ConsultationRequest not found for ID: {}", consultationRequestId);
            return;
        }
        logger.info("sendMessage: Processing message for ConsultationRequest ID: {}, Sender ID: {}",
                consultationRequest.getRequestId(), sender.getUserId());

        User receiver = null;
        // Nếu người gửi là user của consultation request, thì người nhận là assigned
        // staff
        if (consultationRequest.getUser().getUserId().equals(sender.getUserId())) {
            logger.info("sendMessage: Sender (ID: {}) is the consultation request user.", sender.getUserId());
            if (consultationRequest.getAssignedStaff() == null) {
                logger.error("sendMessage: Consultation request {} has no assigned staff. Cannot determine receiver.",
                        consultationRequestId);
                return;
            }
            if (consultationRequest.getAssignedStaff().getUser() == null) {
                logger.error(
                        "sendMessage: Assigned staff for consultation {} (Staff ID: {}) has no associated user account. Cannot determine receiver.",
                        consultationRequestId, consultationRequest.getAssignedStaff().getStaffId());
                return;
            }
            receiver = consultationRequest.getAssignedStaff().getUser();
            logger.info("sendMessage: Receiver determined as assigned staff (User ID: {}).", receiver.getUserId());
        }
        // Nếu người gửi là assigned staff, thì người nhận là user của consultation
        // request
        else if (consultationRequest.getAssignedStaff() != null &&
                consultationRequest.getAssignedStaff().getUser() != null &&
                consultationRequest.getAssignedStaff().getUser().getUserId().equals(sender.getUserId())) {
            logger.info("sendMessage: Sender (ID: {}) is the assigned staff.", sender.getUserId());
            receiver = consultationRequest.getUser();
            logger.info("sendMessage: Receiver determined as consultation request user (User ID: {}).",
                    receiver.getUserId());
        } else {
            logger.error(
                    "sendMessage: Sender (ID: {}) is not part of the consultation (ID: {}). Request User ID: {}, Assigned Staff User ID: {}",
                    sender.getUserId(),
                    consultationRequestId,
                    consultationRequest.getUser().getUserId(),
                    (consultationRequest.getAssignedStaff() != null
                            && consultationRequest.getAssignedStaff().getUser() != null)
                                    ? consultationRequest.getAssignedStaff().getUser().getUserId()
                                    : "N/A or No User Account for Staff");
            return;
        }

        Message message = new Message();
        message.setConsultationRequest(consultationRequest);
        message.setSenderUser(sender);
        message.setReceiverUser(receiver);
        message.setContent(chatMessageDTO.getContent());
        message.setSentAt(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        Message savedMessage = messageService.saveMessage(message); // Bỏ comment và sử dụng biến 'message'

        // Tạo DTO từ tin nhắn đã lưu
        MessageDTO messageDTO = new MessageDTO(savedMessage);

        // Gửi tin nhắn DTO đến client đã gửi (hoặc một topic cụ thể nếu cần)
        // String userSpecificDestination = "/queue/reply-" + principal.getName(); // Ví
        // dụ nếu muốn gửi lại cho người gửi
        // messagingTemplate.convertAndSend(userSpecificDestination, messageDTO);

        // Gửi tin nhắn DTO đến topic chung của cuộc tư vấn
        String destinationTopic = "/topic/consultation/" + consultationRequestId;
        logger.info("sendMessage: Sending DTO message to topic: {}", destinationTopic);
        messagingTemplate.convertAndSend(destinationTopic, messageDTO); // Sửa: simpMessagingTemplate ->
                                                                        // messagingTemplate, destination ->
                                                                        // destinationTopic, savedMessage -> messageDTO

        logger.info("sendMessage: Message DTO sent to topic: {}", destinationTopic);
        logger.info("sendMessage: Message saved with ID: {}", savedMessage.getMessageId());

    }

    @GetMapping("/api/chat/history/{consultationRequestId}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @PathVariable Integer consultationRequestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            logger.warn("getChatHistory: User not authenticated for consultation ID: {}", consultationRequestId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("getChatHistory: Authenticated user not found in database: {}",
                            userDetails.getUsername());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });

        ConsultationRequest consultationRequest = consultationRequestRepository.findById(consultationRequestId)
                .orElseThrow(() -> {
                    logger.warn("getChatHistory: ConsultationRequest not found for ID: {}", consultationRequestId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation request not found");
                });

        // Authorization check
        boolean isUserInvolved = consultationRequest.getUser().getUserId().equals(currentUser.getUserId());
        boolean isStaffInvolved = false;
        if (consultationRequest.getAssignedStaff() != null
                && consultationRequest.getAssignedStaff().getUser() != null) {
            isStaffInvolved = consultationRequest.getAssignedStaff().getUser().getUserId()
                    .equals(currentUser.getUserId());
        }
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if (!isUserInvolved && !isStaffInvolved && !isAdmin) {
            logger.warn("getChatHistory: User {} not authorized to access history for consultation ID: {}",
                    currentUser.getUserId(), consultationRequestId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Message> messages = messageService.getMessagesByConsultationRequestId(consultationRequestId);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(MessageDTO::new)
                .collect(Collectors.toList());

        logger.info("getChatHistory: Successfully retrieved {} messages for consultation ID: {}", messageDTOs.size(),
                consultationRequestId);
        return ResponseEntity.ok(messageDTOs);
    }
}