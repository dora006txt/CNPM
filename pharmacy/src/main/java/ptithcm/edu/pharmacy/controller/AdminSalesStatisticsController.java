package ptithcm.edu.pharmacy.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ptithcm.edu.pharmacy.dto.OverallSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.ProductSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.RevenueByPeriodDTO; // Import the new DTO
import ptithcm.edu.pharmacy.service.SalesStatisticsService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
// Consider adding security annotations like @PreAuthorize("hasRole('ADMIN')") if you use Spring Security
public class AdminSalesStatisticsController {

    private final SalesStatisticsService salesStatisticsService;

    public AdminSalesStatisticsController(SalesStatisticsService salesStatisticsService) {
        this.salesStatisticsService = salesStatisticsService;
    }

    @GetMapping("/overall")
    public ResponseEntity<OverallSalesStatsDTO> getOverallSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Default to past 30 days if no dates are provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }
        
        OverallSalesStatsDTO stats = salesStatisticsService.getOverallSalesStatistics(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<ProductSalesStatsDTO>> getTopSellingProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {

        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        List<ProductSalesStatsDTO> productStats = salesStatisticsService.getTopSellingProducts(startDate, endDate, limit);
        return ResponseEntity.ok(productStats);
    }
    
    // Add more endpoints as needed
    
    // --- New Endpoints for Time-Based Revenue ---

    @GetMapping("/revenue-by-day")
    public ResponseEntity<List<RevenueByPeriodDTO>> getRevenueByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RevenueByPeriodDTO> revenue = salesStatisticsService.getRevenueByDay(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue-by-month")
    public ResponseEntity<List<RevenueByPeriodDTO>> getRevenueByMonth(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) { // Month is optional
        List<RevenueByPeriodDTO> revenue;
        if (month != null) {
            revenue = salesStatisticsService.getRevenueBySpecificMonth(year, month);
        } else {
            revenue = salesStatisticsService.getRevenueByMonth(year);
        }
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue-by-quarter")
    public ResponseEntity<List<RevenueByPeriodDTO>> getRevenueByQuarter(
            @RequestParam int year,
            @RequestParam(required = false) Integer quarter) { // Quarter is optional
        List<RevenueByPeriodDTO> revenue;
        if (quarter != null) {
            // Basic validation for quarter
            if (quarter < 1 || quarter > 4) {
                return ResponseEntity.badRequest().body(Collections.singletonList(new RevenueByPeriodDTO("Invalid quarter value (must be 1-4)", null)));
            }
            revenue = salesStatisticsService.getRevenueBySpecificQuarter(year, quarter);
        } else {
            revenue = salesStatisticsService.getRevenueByQuarter(year);
        }
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue-by-year")
    public ResponseEntity<List<RevenueByPeriodDTO>> getRevenueByYear(
            @RequestParam List<Integer> years) { // Allows fetching for multiple years
        List<RevenueByPeriodDTO> revenue = salesStatisticsService.getRevenueByYear(years);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue-for-year") // Endpoint for a single specific year
    public ResponseEntity<RevenueByPeriodDTO> getRevenueForSpecificYear(
            @RequestParam int year) {
        RevenueByPeriodDTO revenue = salesStatisticsService.getRevenueForSpecificYear(year);
        return ResponseEntity.ok(revenue);
    }
}