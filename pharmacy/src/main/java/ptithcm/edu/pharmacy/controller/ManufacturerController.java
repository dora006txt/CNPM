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
            // Avoid sending back the full Country object if not needed, or create a ManufacturerResponse DTO
            // For now, returning the entity as is (includes country details)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdManufacturer);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            HttpStatus status;
            if (e instanceof EntityNotFoundException) {
                status = HttpStatus.NOT_FOUND; // 404 if country not found
            } else if (e instanceof RuntimeException && e.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT; // 409 if manufacturer name exists
            } else {
                status = HttpStatus.BAD_REQUEST; // 400 for other validation/argument issues
            }
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            // Catch unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the manufacturer.");
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