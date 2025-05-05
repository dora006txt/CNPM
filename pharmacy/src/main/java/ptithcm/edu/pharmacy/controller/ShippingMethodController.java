package ptithcm.edu.pharmacy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import for authorization
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.ShippingMethodDTO;
import ptithcm.edu.pharmacy.service.ShippingMethodService;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-methods") // Base path for shipping methods
public class ShippingMethodController {

    @Autowired
    private ShippingMethodService shippingMethodService;

    // Endpoint for logged-in users (and admin) to view active methods
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()") // Requires any authenticated user
    public ResponseEntity<List<ShippingMethodDTO>> getActiveShippingMethods() {
        List<ShippingMethodDTO> methods = shippingMethodService.findAllActive();
        return ResponseEntity.ok(methods);
    }

    // Endpoint for admin to view ALL methods (including inactive)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<List<ShippingMethodDTO>> getAllShippingMethods() {
        List<ShippingMethodDTO> methods = shippingMethodService.findAll();
        return ResponseEntity.ok(methods);
    }

    // Endpoint for admin to view a specific method by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<ShippingMethodDTO> getShippingMethodById(@PathVariable Integer id) {
        ShippingMethodDTO method = shippingMethodService.findById(id);
        return ResponseEntity.ok(method);
    }

    // Endpoint for admin to add a new shipping method
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<ShippingMethodDTO> createShippingMethod(@RequestBody ShippingMethodDTO shippingMethodDTO) {
        ShippingMethodDTO createdMethod = shippingMethodService.save(shippingMethodDTO);
        return new ResponseEntity<>(createdMethod, HttpStatus.CREATED);
    }

    // Endpoint for admin to update an existing shipping method
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<ShippingMethodDTO> updateShippingMethod(@PathVariable Integer id, @RequestBody ShippingMethodDTO shippingMethodDTO) {
        ShippingMethodDTO updatedMethod = shippingMethodService.update(id, shippingMethodDTO);
        return ResponseEntity.ok(updatedMethod);
    }

     // Endpoint for admin to activate/deactivate a shipping method (using PATCH for partial update)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<Void> setShippingMethodStatus(@PathVariable Integer id, @RequestParam boolean active) {
        shippingMethodService.setActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }


    // Endpoint for admin to delete a shipping method
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- Change hasRole to hasAuthority
    public ResponseEntity<Void> deleteShippingMethod(@PathVariable Integer id) {
        shippingMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}