package ptithcm.edu.pharmacy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// --- Add logging imports ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// --- End logging imports ---

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Add this import
import ptithcm.edu.pharmacy.dto.ProductRequest;
import ptithcm.edu.pharmacy.dto.ProductResponse; // Import Response DTO
import ptithcm.edu.pharmacy.dto.CategoryResponse;
import ptithcm.edu.pharmacy.dto.BrandResponse;
import ptithcm.edu.pharmacy.dto.ManufacturerResponse;
import ptithcm.edu.pharmacy.dto.CountryResponse;
// Remove unused Entity/DTO imports if mapping is fully in service
// import ptithcm.edu.pharmacy.entity.Product;
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.entity.Manufacturer;
import ptithcm.edu.pharmacy.entity.Product;
import ptithcm.edu.pharmacy.entity.Country;
import ptithcm.edu.pharmacy.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Remove unused stream/Collectors if not used here anymore
// import java.util.stream.Collectors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    // --- Add Logger instance ---
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    // --- End Logger instance ---

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to create product: {}", productRequest.getName());
        Product savedProduct = productService.createProduct(productRequest);
        ProductResponse responseDto = mapProductToResponseDto(savedProduct);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        log.info("Received request to get product by ID: {}", id);
        return productService.findProductById(id)
                .map(this::mapProductToResponseDto)
                .map(responseDto -> {
                    log.info("Product found with ID: {}", id);
                    return ResponseEntity.ok(responseDto);
                })
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
                });
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Received request to get all products");
        List<Product> products = productService.findAllProducts();
        List<ProductResponse> responseDtos = products.stream()
                .map(this::mapProductToResponseDto)
                .collect(Collectors.toList());
        log.info("Returning {} products", responseDtos.size());
        return ResponseEntity.ok(responseDtos);
    }

    // --- New Endpoint: Update Product ---
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to update product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, productRequest);
        ProductResponse responseDto = mapProductToResponseDto(updatedProduct);
        log.info("Product updated successfully with ID: {}", id);
        return ResponseEntity.ok(responseDto);
    }
    // --- End Update Product ---

    // --- New Endpoint: Delete Product ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        log.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
    // --- End Delete Product ---


    // --- Helper mapping functions ---
    private ProductResponse mapProductToResponseDto(Product product) {
        // Ensure related entities are not null before mapping
        ManufacturerResponse manufacturerDto = product.getManufacturer() != null ? mapManufacturerToResponseDto(product.getManufacturer()) : null;
        CategoryResponse categoryDto = product.getCategory() != null ? mapCategoryToResponseDto(product.getCategory()) : null;
        BrandResponse brandDto = product.getBrand() != null ? mapBrandToResponseDto(product.getBrand()) : null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                // Add other fields from Product entity to ProductResponse DTO as needed
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .ingredients(product.getIngredients())
                .usageInstructions(product.getUsageInstructions())
                .contraindications(product.getContraindications())
                .sideEffects(product.getSideEffects())
                .storageConditions(product.getStorageConditions())
                .packaging(product.getPackaging())
                .unit(product.getUnit())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus()) // Assuming ProductResponse has status
                .isPrescriptionRequired(product.isPrescriptionRequired()) // Assuming ProductResponse has this field
                .averageRating(product.getAverageRating()) // Assuming ProductResponse has this field
                .reviewCount(product.getReviewCount()) // Assuming ProductResponse has this field
                .createdAt(product.getCreatedAt()) // Assuming ProductResponse has this field
                .updatedAt(product.getUpdatedAt()) // Assuming ProductResponse has this field
                // Mapped related objects
                .manufacturer(manufacturerDto)
                .category(categoryDto)
                .brand(brandDto)
                .build();
    }

    private CategoryResponse mapCategoryToResponseDto(Category category) {
        if (category == null) return null;
        return CategoryResponse.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                // Add other category fields if needed in response
                .build();
    }

     private BrandResponse mapBrandToResponseDto(Brand brand) {
        if (brand == null) return null;
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                // Add other brand fields if needed in response
                .build();
    }

     private ManufacturerResponse mapManufacturerToResponseDto(Manufacturer manufacturer) {
        if (manufacturer == null) return null;
        CountryResponse countryDto = manufacturer.getCountry() != null ? mapCountryToResponseDto(manufacturer.getCountry()) : null;
        return ManufacturerResponse.builder()
                .id(manufacturer.getId())
                .name(manufacturer.getName())
                .country(countryDto)
                // Add other manufacturer fields if needed in response
                .build();
    }

     private CountryResponse mapCountryToResponseDto(Country country) {
        if (country == null) return null;
        return CountryResponse.builder()
                .id(country.getCountryId())
                .countryCode(country.getCountryCode())
                .countryName(country.getCountryName())
                .build();
    }
    // --- End Helper mapping functions ---


    // --- Exception Handlers ---
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.warn("Validation error - Field: {}, Message: {}", fieldName, errorMessage);
        });
        return errors;
    }

    // Handles exceptions like ResponseStatusException thrown from the service
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason());
        error.put("status", String.valueOf(ex.getStatusCode().value()));
        log.warn("API error occurred: Status={}, Reason={}", ex.getStatusCode(), ex.getReason());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: ", ex);
        Map<String, String> error = new HashMap<>();
        error.put("message", "An internal server error occurred. Please try again later.");
        // Optionally include ex.getMessage() in non-production environments for easier debugging
        // error.put("details", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // --- End Exception Handlers ---

} // End class