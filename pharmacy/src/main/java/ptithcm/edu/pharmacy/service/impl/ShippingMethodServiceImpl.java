package ptithcm.edu.pharmacy.service.impl;

import org.springframework.beans.factory.annotation.Autowired; // Or constructor injection
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.ShippingMethodDTO;
import ptithcm.edu.pharmacy.entity.ShippingMethod;
import ptithcm.edu.pharmacy.repository.ShippingMethodRepository;
import ptithcm.edu.pharmacy.service.ShippingMethodService;
// Import a mapper or handle mapping manually
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException; // Or your custom exception

@Service
public class ShippingMethodServiceImpl implements ShippingMethodService {

    @Autowired // Consider constructor injection
    private ShippingMethodRepository shippingMethodRepository;

    // Assume you have a mapper or map manually
    private ShippingMethodDTO convertToDTO(ShippingMethod entity) {
        // Manual mapping example
        ShippingMethodDTO dto = new ShippingMethodDTO();
        dto.setMethodId(entity.getMethodId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setBaseCost(entity.getBaseCost());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    private ShippingMethod convertToEntity(ShippingMethodDTO dto) {
        // Manual mapping example
        ShippingMethod entity = new ShippingMethod();
        // methodId is usually set by JPA or handled during update
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setBaseCost(dto.getBaseCost());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true); // Default to active if null
        return entity;
    }


    @Override
    public List<ShippingMethodDTO> findAllActive() {
        return shippingMethodRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShippingMethodDTO> findAll() {
         return shippingMethodRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ShippingMethodDTO findById(Integer id) {
        ShippingMethod entity = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShippingMethod not found with id: " + id));
        return convertToDTO(entity);
    }

    @Override
    public ShippingMethodDTO save(ShippingMethodDTO shippingMethodDTO) {
        ShippingMethod entity = convertToEntity(shippingMethodDTO);
        entity.setMethodId(null); // Ensure it's a new entity
        ShippingMethod savedEntity = shippingMethodRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public ShippingMethodDTO update(Integer id, ShippingMethodDTO shippingMethodDTO) {
         ShippingMethod existingEntity = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShippingMethod not found with id: " + id));

        // Update fields from DTO
        existingEntity.setName(shippingMethodDTO.getName());
        existingEntity.setDescription(shippingMethodDTO.getDescription());
        existingEntity.setBaseCost(shippingMethodDTO.getBaseCost());
        if (shippingMethodDTO.getIsActive() != null) { // Allow updating active status
             existingEntity.setIsActive(shippingMethodDTO.getIsActive());
        }

        ShippingMethod updatedEntity = shippingMethodRepository.save(existingEntity);
        return convertToDTO(updatedEntity);
    }

     @Override
    public void setActiveStatus(Integer id, boolean isActive) {
        ShippingMethod entity = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ShippingMethod not found with id: " + id));
        entity.setIsActive(isActive);
        shippingMethodRepository.save(entity);
    }


    @Override
    public void delete(Integer id) {
         if (!shippingMethodRepository.existsById(id)) {
            throw new EntityNotFoundException("ShippingMethod not found with id: " + id);
        }
        // Consider soft delete by setting isActive = false instead of hard delete
        // setActiveStatus(id, false);
         shippingMethodRepository.deleteById(id); // Hard delete
    }
}