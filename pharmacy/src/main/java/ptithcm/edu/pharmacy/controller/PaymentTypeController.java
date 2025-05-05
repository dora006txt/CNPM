package ptithcm.edu.pharmacy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.PaymentTypeDTO;
import ptithcm.edu.pharmacy.service.PaymentTypeService;

import java.util.List;

@RestController
@RequestMapping("/api") // Base path
@RequiredArgsConstructor
public class PaymentTypeController {

    private final PaymentTypeService paymentTypeService;

    // --- Public/Authenticated User Endpoint ---

    // GET /api/payment-types - View active payment types (for logged-in users)
    @GetMapping("/payment-types")
    public ResponseEntity<List<PaymentTypeDTO>> getActivePaymentTypes() {
        List<PaymentTypeDTO> paymentTypes = paymentTypeService.findAllActivePaymentTypes();
        return ResponseEntity.ok(paymentTypes);
    }

    // --- Admin Endpoints ---

    // GET /api/admin/payment-types - View all payment types (Admin only)
    @GetMapping("/admin/payment-types")
    // @PreAuthorize("hasAuthority('ADMIN')") // Alternative security
    public ResponseEntity<List<PaymentTypeDTO>> getAllPaymentTypes() {
        List<PaymentTypeDTO> paymentTypes = paymentTypeService.findAllPaymentTypes();
        return ResponseEntity.ok(paymentTypes);
    }

    // GET /api/admin/payment-types/{id} - View specific payment type (Admin only)
    @GetMapping("/admin/payment-types/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaymentTypeDTO> getPaymentTypeById(@PathVariable Integer id) {
        PaymentTypeDTO paymentType = paymentTypeService.findPaymentTypeById(id);
        return ResponseEntity.ok(paymentType);
    }

    // POST /api/admin/payment-types - Add new payment type (Admin only)
    @PostMapping("/admin/payment-types")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaymentTypeDTO> createPaymentType(@RequestBody PaymentTypeDTO paymentTypeDTO) {
        PaymentTypeDTO createdPaymentType = paymentTypeService.createPaymentType(paymentTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentType);
    }

    // PUT /api/admin/payment-types/{id} - Update payment type (Admin only)
    @PutMapping("/admin/payment-types/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaymentTypeDTO> updatePaymentType(@PathVariable Integer id, @RequestBody PaymentTypeDTO paymentTypeDTO) {
        PaymentTypeDTO updatedPaymentType = paymentTypeService.updatePaymentType(id, paymentTypeDTO);
        return ResponseEntity.ok(updatedPaymentType);
    }

    // DELETE /api/admin/payment-types/{id} - Delete (deactivate) payment type (Admin only)
    @DeleteMapping("/admin/payment-types/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletePaymentType(@PathVariable Integer id) {
        paymentTypeService.deletePaymentType(id);
        return ResponseEntity.noContent().build();
    }
}