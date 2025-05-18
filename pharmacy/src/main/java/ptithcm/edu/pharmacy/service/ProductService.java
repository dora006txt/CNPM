package ptithcm.edu.pharmacy.service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ptithcm.edu.pharmacy.dto.ProductRequest;
import ptithcm.edu.pharmacy.repository.BrandRepository;
import ptithcm.edu.pharmacy.repository.CategoryRepository;
import ptithcm.edu.pharmacy.repository.ManufacturerRepository;
import ptithcm.edu.pharmacy.repository.ProductRepository;

import java.text.Normalizer; // For slug generation
import java.util.Locale; // For slug generation
import java.util.regex.Pattern;
import java.util.Collections; // Added import
import java.util.Map; // Added import
import java.util.Objects; // Added import
import java.util.stream.Collectors; // Added import

import org.springframework.data.domain.PageRequest; // Added import
import org.springframework.data.domain.Pageable; // Added import

import ptithcm.edu.pharmacy.entity.Product; // Ensure Product is imported
import ptithcm.edu.pharmacy.entity.Category;
import ptithcm.edu.pharmacy.entity.Brand;
import ptithcm.edu.pharmacy.entity.Manufacturer;

import java.util.List; // Import List
import java.util.Optional; // Import Optional

import ptithcm.edu.pharmacy.entity.ProductStatus; // Ensure this is imported

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ManufacturerRepository manufacturerRepository;
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    // Simple slug generation utility method
    private String generateSlug(String input) {
        if (input == null)
            return "";
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    @Transactional // Keep transaction open for potential lazy loading during mapping
    public Product createProduct(ProductRequest request) { // Return the entity
        // Check for existing name (case-insensitive)
        productRepository.findByNameIgnoreCase(request.getName()).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name already exists");
        });

        // Check for existing SKU if provided (case-insensitive)
        if (request.getSku() != null && !request.getSku().trim().isEmpty()) {
            productRepository.findBySkuIgnoreCase(request.getSku()).ifPresent(p -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product SKU already exists");
            });
        }

        // Generate slug and check for uniqueness
        String slug = generateSlug(request.getName());
        if (productRepository.existsBySlugIgnoreCase(slug)) {
            // Handle slug collision, e.g., append a number or throw an error
            // For simplicity, we'll throw an error here. Consider a more robust strategy.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Generated slug already exists. Try a slightly different name.");
        }

        // Fetch related entities
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + request.getCategoryId()));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Brand not found with ID: " + request.getBrandId()));
        Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Manufacturer not found with ID: " + request.getManufacturerId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(slug); // Use the generated and checked slug
        product.setSku(request.getSku()); // Can be null
        product.setDescription(request.getDescription());
        product.setIngredients(request.getIngredients());
        product.setUsageInstructions(request.getUsageInstructions());
        product.setContraindications(request.getContraindications());
        product.setSideEffects(request.getSideEffects());
        product.setStorageConditions(request.getStorageConditions());
        product.setPackaging(request.getPackaging());
        product.setUnit(request.getUnit());
        product.setImageUrl(request.getImageUrl()); // Can be null

        // Set defaults if not provided in request
        product.setPrescriptionRequired(
                request.getIsPrescriptionRequired() != null ? request.getIsPrescriptionRequired() : false);
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.ACTIVE);

        // Set relationships
        product.setCategory(category);
        product.setBrand(brand);
        product.setManufacturer(manufacturer);

        // Default values for new fields (already set in entity definition)
        // product.setAverageRating(BigDecimal.ZERO);
        // product.setReviewCount(0);

        // Timestamps are handled by @CreationTimestamp/@UpdateTimestamp

        return productRepository.save(product); // Return the saved entity
    }

    // --- New Method: Update Product ---
    @Transactional
    public Product updateProduct(Integer id, ProductRequest request) {
        // 1. Find the existing product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        // 2. Validate Name uniqueness (if changed)
        if (!existingProduct.getName().equalsIgnoreCase(request.getName())) {
            productRepository.findByNameIgnoreCase(request.getName()).ifPresent(p -> {
                if (!p.getId().equals(id)) { // Ensure it's not the same product
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name already exists");
                }
            });
            existingProduct.setName(request.getName());
            // Regenerate slug if name changes
            String newSlug = generateSlug(request.getName());
            // Check slug uniqueness (excluding self)
            if (productRepository.existsBySlugIgnoreCase(newSlug)
                    && !existingProduct.getSlug().equalsIgnoreCase(newSlug)) {
                // Handle slug collision
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Generated slug from new name already exists.");
            }
            existingProduct.setSlug(newSlug);
        }

        // 3. Validate SKU uniqueness (if changed and not empty)
        String requestedSku = request.getSku() != null ? request.getSku().trim() : null;
        String existingSku = existingProduct.getSku();

        if (requestedSku != null && !requestedSku.isEmpty() && !requestedSku.equalsIgnoreCase(existingSku)) {
            productRepository.findBySkuIgnoreCase(requestedSku).ifPresent(p -> {
                if (!p.getId().equals(id)) { // Ensure it's not the same product
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product SKU already exists");
                }
            });
            existingProduct.setSku(requestedSku);
        } else if (requestedSku == null || requestedSku.isEmpty()) {
            existingProduct.setSku(null); // Allow setting SKU to null/empty
        }

        // 4. Fetch and update related entities if IDs changed
        if (!existingProduct.getCategory().getCategoryId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Category not found with ID: " + request.getCategoryId()));
            existingProduct.setCategory(category);
        }
        if (!existingProduct.getBrand().getId().equals(request.getBrandId())) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Brand not found with ID: " + request.getBrandId()));
            existingProduct.setBrand(brand);
        }
        if (!existingProduct.getManufacturer().getId().equals(request.getManufacturerId())) {
            Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Manufacturer not found with ID: " + request.getManufacturerId()));
            existingProduct.setManufacturer(manufacturer);
        }

        // 5. Update other fields from the request
        existingProduct.setDescription(request.getDescription());
        existingProduct.setIngredients(request.getIngredients());
        existingProduct.setUsageInstructions(request.getUsageInstructions());
        existingProduct.setContraindications(request.getContraindications());
        existingProduct.setSideEffects(request.getSideEffects());
        existingProduct.setStorageConditions(request.getStorageConditions());
        existingProduct.setPackaging(request.getPackaging());
        existingProduct.setUnit(request.getUnit());
        existingProduct.setImageUrl(request.getImageUrl());
        existingProduct.setPrescriptionRequired(
                request.getIsPrescriptionRequired() != null ? request.getIsPrescriptionRequired()
                        : existingProduct.isPrescriptionRequired());
        existingProduct.setStatus(request.getStatus() != null ? request.getStatus() : existingProduct.getStatus());

        // AverageRating and ReviewCount are typically updated via other processes
        // (e.g., reviews)
        // Timestamps are handled by @UpdateTimestamp

        // 6. Save the updated product
        return productRepository.save(existingProduct);
    }
    // --- End Update Product ---

    // --- New Method: Delete Product ---
    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    // --- End Delete Product ---

    // --- Existing Method: Find Product by ID ---
    @Transactional(readOnly = true) // Use readOnly for query operations
    public Optional<Product> findProductById(Integer id) {
        // Fetch the product and potentially initialize related entities if needed later
        // for mapping
        // findById already returns Optional<Product>
        return productRepository.findById(id);
        // Note: If mapping required initializing lazy fields *within* the service,
        // you might need to explicitly access them here, e.g.,
        // product.getManufacturer().getName()
        // But since mapping happens in the controller, returning the Optional is fine.
    }

    // --- Existing Method: Find All Products ---
    @Transactional(readOnly = true) // Use readOnly for query operations
    public List<Product> findAllProducts() {
        // Fetch all products. Lazy relationships will be handled during mapping in the
        // controller.
        return productRepository.findAll();
    }

    // Existing method for top selling products (ensure it's suitable or adjust as
    // needed)
    @Transactional(readOnly = true)
    public List<Product> getTopSellingProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        // Corrected to use productRepository
        List<Object[]> results = productRepository.findTopSellingProductIdsAndQuantities(pageable);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> productIds = results.stream()
                .map(result -> (Integer) result[0])
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        // To maintain the order of best-selling, we need to re-order `products` based
        // on `productIds`
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        return productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull) // Filter out any nulls if a product ID wasn't found (should not happen
                                          // ideally)
                .collect(Collectors.toList());
    }

}
