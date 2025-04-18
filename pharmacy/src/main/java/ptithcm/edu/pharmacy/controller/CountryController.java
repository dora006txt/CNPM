package ptithcm.edu.pharmacy.controller;

import jakarta.validation.Valid; // Import for validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ptithcm.edu.pharmacy.dto.CountryRequest;
import ptithcm.edu.pharmacy.entity.Country;
import ptithcm.edu.pharmacy.service.CountryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @PostMapping
    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryRequest countryRequest) { // Add @Valid
        try {
            Country createdCountry = countryService.createCountry(countryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCountry);
        } catch (RuntimeException e) { // Catch specific exceptions from service
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            // Use 409 Conflict for duplicate errors
            HttpStatus status = (e instanceof RuntimeException && e.getMessage().contains("already exists"))
                                 ? HttpStatus.CONFLICT
                                 : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            // Catch unexpected errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while creating the country.");
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