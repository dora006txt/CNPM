package ptithcm.edu.pharmacy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.entity.ConsultationRequest;
import ptithcm.edu.pharmacy.entity.Message;
import ptithcm.edu.pharmacy.repository.ConsultationRequestRepository;
import ptithcm.edu.pharmacy.repository.MessageRepository;
import ptithcm.edu.pharmacy.service.MessageService;
import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired // Thêm Autowired cho ConsultationRequestRepository
    private ConsultationRequestRepository consultationRequestRepository;

    @Override
    public Message saveMessage(Message message) {
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        // Thêm logic cập nhật lastUpdated cho ConsultationRequest
        ConsultationRequest consultationRequest = message.getConsultationRequest();
        if (consultationRequest != null) {
            consultationRequest.setLastUpdated(LocalDateTime.now());
            consultationRequestRepository.save(consultationRequest); // Lưu lại thay đổi
        }
        return messageRepository.save(message);
    }
}