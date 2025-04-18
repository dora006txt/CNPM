package ptithcm.edu.pharmacy.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.BrandRequest;
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.service.BrandService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandRequest request) {
        try {
            Brand createdBrand = brandService.createBrand(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = (e instanceof RuntimeException && e.getMessage().contains("already exists"))
                                 ? HttpStatus.CONFLICT // 409 for duplicate
                                 : HttpStatus.BAD_REQUEST; // 400 for other validation/argument issues
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            // Catch unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the brand.");
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