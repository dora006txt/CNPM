package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.entity.Message;
import java.util.List; // Thêm import này

public interface MessageService {
    Message saveMessage(Message message);

    List<Message> getMessagesByConsultationRequestId(Integer consultationRequestId);
}