package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.ShippingMethodDTO;
import java.util.List;

public interface ShippingMethodService {
    List<ShippingMethodDTO> findAllActive(); // For logged-in users
    List<ShippingMethodDTO> findAll(); // For admin
    ShippingMethodDTO findById(Integer id);
    ShippingMethodDTO save(ShippingMethodDTO shippingMethodDTO);
    ShippingMethodDTO update(Integer id, ShippingMethodDTO shippingMethodDTO);
    void delete(Integer id);
    void setActiveStatus(Integer id, boolean isActive); // Method to activate/deactivate
}