package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.PaymentTypeDTO;

import java.util.List;

public interface PaymentTypeService {
    // For logged-in users
    List<PaymentTypeDTO> findAllActivePaymentTypes();

    // For Admins
    List<PaymentTypeDTO> findAllPaymentTypes();
    PaymentTypeDTO findPaymentTypeById(Integer id);
    PaymentTypeDTO createPaymentType(PaymentTypeDTO paymentTypeDTO);
    PaymentTypeDTO updatePaymentType(Integer id, PaymentTypeDTO paymentTypeDTO);
    void deletePaymentType(Integer id);
}