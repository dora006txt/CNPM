package ptithcm.edu.pharmacy.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.ManufacturerRequest;
import ptithcm.edu.pharmacy.entity.Manufacturer;
import ptithcm.edu.pharmacy.service.ManufacturerService;

import java.util.HashMap;
import java.util.List; // Added import
import java.util.Map;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    @PostMapping
    public ResponseEntity<?> createManufacturer(@Valid @RequestBody ManufacturerRequest request) {
        try {
            Manufacturer createdManufacturer = manufacturerService.createManufacturer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdManufacturer);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            HttpStatus status;
            if (e instanceof EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND; // 404 if country not found
            } else if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT; // 409 if manufacturer name exists
            } else if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST; // 400 for validation/argument issues
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500 for other runtime exceptions
            }
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the manufacturer.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Manufacturer>> getAllManufacturers() {
        List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
        return ResponseEntity.ok(manufacturers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getManufacturerById(@PathVariable Integer id) {
        try {
            Manufacturer manufacturer = manufacturerService.getManufacturerById(id);
            return ResponseEntity.ok(manufacturer);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while retrieving the manufacturer.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateManufacturer(@PathVariable Integer id, @Valid @RequestBody ManufacturerRequest request) {
        try {
            Manufacturer updatedManufacturer = manufacturerService.updateManufacturer(id, request);
            return ResponseEntity.ok(updatedManufacturer);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            HttpStatus status;
            if (e instanceof EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND; // 404 if manufacturer or country not found
            } else if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT; // 409 if manufacturer name exists
            } else if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST; // 400 for validation/argument issues
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500
            }
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while updating the manufacturer.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManufacturer(@PathVariable Integer id) {
        try {
            manufacturerService.deleteManufacturer(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while deleting the manufacturer.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Exception handler for validation errors (@Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    // Optional: Add endpoints for GET, PUT, DELETE later
}