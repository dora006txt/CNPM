package ptithcm.edu.pharmacy.dto;

import ptithcm.edu.pharmacy.entity.Message;
// Giả sử bạn có User entity, không cần import nếu cùng package hoặc đã import ở nơi khác
// import ptithcm.edu.pharmacy.entity.User; 

import java.time.LocalDateTime; // Thay đổi từ java.util.Date

public class MessageDTO {
    private Long id;
    private String content;
    private Integer senderId; // Thay đổi từ Long sang Integer
    private String senderFullName;
    private Integer receiverId; // Thay đổi từ Long sang Integer
    private String receiverFullName;
    private Integer consultationRequestId; // Thay đổi từ Long sang Integer
    private LocalDateTime sentAt; // Thay đổi từ Date sang LocalDateTime
    // Thêm các trường khác cần thiết từ Message

    public MessageDTO(Message message) {
        this.id = message.getMessageId(); // Sửa: getId() -> getMessageId()
        this.content = message.getContent();
        if (message.getSenderUser() != null) { // Sửa: getSender() -> getSenderUser()
            this.senderId = message.getSenderUser().getUserId();
            this.senderFullName = message.getSenderUser().getFullName();
        }
        if (message.getReceiverUser() != null) { // Sửa: getReceiver() -> getReceiverUser()
            this.receiverId = message.getReceiverUser().getUserId();
            this.receiverFullName = message.getReceiverUser().getFullName();
        }
        if (message.getConsultationRequest() != null) {
            this.consultationRequestId = message.getConsultationRequest().getRequestId();
        }
        this.sentAt = message.getSentAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSenderId() { return senderId; } // Sửa kiểu trả về
    public void setSenderId(Integer senderId) { this.senderId = senderId; } // Sửa kiểu tham số
    public String getSenderFullName() { return senderFullName; }
    public void setSenderFullName(String senderFullName) { this.senderFullName = senderFullName; }
    public Integer getReceiverId() { return receiverId; } // Sửa kiểu trả về
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; } // Sửa kiểu tham số
    public String getReceiverFullName() { return receiverFullName; }
    public void setReceiverFullName(String receiverFullName) { this.receiverFullName = receiverFullName; }
    public Integer getConsultationRequestId() { return consultationRequestId; } // Sửa kiểu trả về
    public void setConsultationRequestId(Integer consultationRequestId) { this.consultationRequestId = consultationRequestId; } // Sửa kiểu tham số
    public LocalDateTime getSentAt() { return sentAt; } // Sửa kiểu trả về
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; } // Sửa kiểu tham số
}