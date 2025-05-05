package ptithcm.edu.pharmacy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ptithcm.edu.pharmacy.dto.PaymentTypeDTO;
import ptithcm.edu.pharmacy.entity.PaymentType;
import ptithcm.edu.pharmacy.repository.PaymentTypeRepository;
import ptithcm.edu.pharmacy.service.PaymentTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTypeServiceImpl implements PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Override
    public List<PaymentTypeDTO> findAllActivePaymentTypes() {
        return paymentTypeRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentTypeDTO> findAllPaymentTypes() {
        return paymentTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentTypeDTO findPaymentTypeById(Integer id) {
        PaymentType paymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentType not found with id: " + id));
        return mapToDTO(paymentType);
    }

    @Override
    @Transactional
    public PaymentTypeDTO createPaymentType(PaymentTypeDTO paymentTypeDTO) {
        // Check if type name already exists (case-insensitive)
        paymentTypeRepository.findByTypeNameIgnoreCase(paymentTypeDTO.getTypeName())
                .ifPresent(pt -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment type name '" + paymentTypeDTO.getTypeName() + "' already exists.");
                });

        PaymentType paymentType = mapToEntity(paymentTypeDTO);
        paymentType.setIsActive(paymentTypeDTO.getIsActive() != null ? paymentTypeDTO.getIsActive() : true); // Default to active if not specified
        PaymentType savedPaymentType = paymentTypeRepository.save(paymentType);
        return mapToDTO(savedPaymentType);
    }

    @Override
    @Transactional
    public PaymentTypeDTO updatePaymentType(Integer id, PaymentTypeDTO paymentTypeDTO) {
        PaymentType existingPaymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentType not found with id: " + id));

        // Check if the new type name conflicts with another existing type
        paymentTypeRepository.findByTypeNameIgnoreCase(paymentTypeDTO.getTypeName())
                .ifPresent(pt -> {
                    if (!pt.getPaymentTypeId().equals(id)) { // If it's a different payment type
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment type name '" + paymentTypeDTO.getTypeName() + "' already exists.");
                    }
                });

        existingPaymentType.setTypeName(paymentTypeDTO.getTypeName());
        existingPaymentType.setDescription(paymentTypeDTO.getDescription());
        if (paymentTypeDTO.getIsActive() != null) {
            existingPaymentType.setIsActive(paymentTypeDTO.getIsActive());
        }
        PaymentType updatedPaymentType = paymentTypeRepository.save(existingPaymentType);
        return mapToDTO(updatedPaymentType);
    }

    @Override
    @Transactional
    public void deletePaymentType(Integer id) {
        PaymentType paymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentType not found with id: " + id));

        // Option 1: Deactivate instead of hard delete (safer if linked to orders)
        paymentType.setIsActive(false);
        paymentTypeRepository.save(paymentType);

        // Option 2: Hard delete (use with caution)
        // try {
        //     paymentTypeRepository.delete(paymentType);
        // } catch (DataIntegrityViolationException e) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete payment type. It might be associated with existing orders or user payment methods.");
        // }
    }

    // --- Helper Methods ---
    private PaymentTypeDTO mapToDTO(PaymentType paymentType) {
        return new PaymentTypeDTO(
                paymentType.getPaymentTypeId(),
                paymentType.getTypeName(),
                paymentType.getDescription(),
                paymentType.getIsActive()
        );
    }

    private PaymentType mapToEntity(PaymentTypeDTO dto) {
        PaymentType paymentType = new PaymentType();
        // ID is not set here as it's generated or comes from the path variable for update
        paymentType.setTypeName(dto.getTypeName());
        paymentType.setDescription(dto.getDescription());
        paymentType.setIsActive(dto.getIsActive()); // Will be handled in create/update logic
        return paymentType;
    }
}