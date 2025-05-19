package ptithcm.edu.pharmacy.dto;

public class ConsultationRequestDTO {
    private String requestType; // Loại yêu cầu, ví dụ: "GENERAL_ADVICE", "PRESCRIPTION_RENEWAL"
    private String userMessage; // Nội dung tin nhắn từ người dùng
    private Integer branchId;   // ID của chi nhánh (nếu có, có thể null)
    // private String userId; // Thường được lấy từ Principal, không cần thiết trong DTO nếu controller xử lý

    // Constructors
    public ConsultationRequestDTO() {
    }

    public ConsultationRequestDTO(String requestType, String userMessage, Integer branchId) {
        this.requestType = requestType;
        this.userMessage = userMessage;
        this.branchId = branchId;
    }

    // Getters and Setters
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    // toString() method (optional, for debugging)
    @Override
    public String toString() {
        return "ConsultationRequestDTO{" +
                "requestType='" + requestType + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", branchId=" + branchId +
                '}';
    }
}