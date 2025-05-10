package ptithcm.edu.pharmacy.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.OverallSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.ProductSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.RevenueByPeriodDTO; // Ensure this is imported
import ptithcm.edu.pharmacy.entity.Product;
import ptithcm.edu.pharmacy.repository.OrderRepository;
import ptithcm.edu.pharmacy.repository.OrderItemRepository;
import ptithcm.edu.pharmacy.repository.ProductRepository;
import ptithcm.edu.pharmacy.service.SalesStatisticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SalesStatisticsServiceImpl implements SalesStatisticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public SalesStatisticsServiceImpl(OrderRepository orderRepository,
                                      OrderItemRepository orderItemRepository,
                                      ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OverallSalesStatsDTO getOverallSalesStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.sumTotalAmountByOrderDateBetween(startDateTime, endDateTime);
        Long totalOrders = orderRepository.countByOrderDateBetween(startDateTime, endDateTime);

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        if (totalOrders == null) {
            totalOrders = 0L;
        }

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }
        
        return new OverallSalesStatsDTO(totalRevenue, totalOrders, averageOrderValue);
    }

    @Override
    public List<ProductSalesStatsDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = orderItemRepository.findTopSellingProductsByDateRange(
                startDateTime, endDateTime, PageRequest.of(0, limit));

        return results.stream().map(result -> {
            Integer productId = (Integer) result[0];
            String productName = productRepository.findById(productId)
                                                 .map(Product::getName)
                                                 .orElse("Unknown Product");
            Long totalQuantitySold = ((Number) result[1]).longValue(); 
            BigDecimal totalRevenueFromProduct = (BigDecimal) result[2];
            
            return new ProductSalesStatsDTO(productId, productName, totalQuantitySold, totalRevenueFromProduct);
        }).collect(Collectors.toList());
    }

    // --- Implementation for New Time-Based Revenue Methods ---

    @Override
    public List<RevenueByPeriodDTO> getRevenueByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Object[]> results = orderRepository.findRevenueByDayBetween(startDateTime, endDateTime);
        return results.stream()
                .map(result -> new RevenueByPeriodDTO(
                        ((java.sql.Date) result[0]).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), // Format date as YYYY-MM-DD
                        (BigDecimal) result[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueByPeriodDTO> getRevenueByMonth(int year) {
        List<Object[]> results = orderRepository.findRevenueByMonthForYear(year);
        return results.stream()
                .map(result -> {
                    // result[0] is year (Integer), result[1] is month (Integer)
                    Integer resultYear = (Integer) result[0];
                    Integer monthNumber = (Integer) result[1];
                    String monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    return new RevenueByPeriodDTO(
                            String.format("%s %d", monthName, resultYear),
                            (BigDecimal) result[2]);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RevenueByPeriodDTO> getRevenueBySpecificMonth(int year, int month) {
        BigDecimal revenue = orderRepository.findRevenueForSpecificMonth(year, month);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        RevenueByPeriodDTO dto = new RevenueByPeriodDTO(String.format("%s %d", monthName, year), revenue);
        return List.of(dto); // Return as a list for consistency with controller
    }

    @Override
    public List<RevenueByPeriodDTO> getRevenueByQuarter(int year) {
        List<Object[]> results = orderRepository.findRevenueByQuarterForYear(year);
        return results.stream()
                .map(result -> {
                    // result[0] is year (Integer), result[1] is quarter (Integer)
                    Integer resultYear = (Integer) result[0];
                    Integer quarterNumber = (Integer) result[1];
                    return new RevenueByPeriodDTO(
                            String.format("Q%d %d", quarterNumber, resultYear),
                            (BigDecimal) result[2]);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueByPeriodDTO> getRevenueBySpecificQuarter(int year, int quarter) {
        BigDecimal revenue = orderRepository.findRevenueForSpecificQuarter(year, quarter);
         if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        RevenueByPeriodDTO dto = new RevenueByPeriodDTO(String.format("Q%d %d", quarter, year), revenue);
        return List.of(dto); // Return as a list
    }


    @Override
    public List<RevenueByPeriodDTO> getRevenueByYear(List<Integer> years) {
        if (years == null || years.isEmpty()) {
            return new ArrayList<>();
        }
        List<Object[]> results = orderRepository.findRevenueByYearIn(years);
        return results.stream()
                .map(result -> new RevenueByPeriodDTO(
                        ((Integer) result[0]).toString(), // Year
                        (BigDecimal) result[1]))
                .collect(Collectors.toList());
    }

    @Override
    public RevenueByPeriodDTO getRevenueForSpecificYear(int year) {
        BigDecimal revenue = orderRepository.findRevenueForSpecificYear(year);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        return new RevenueByPeriodDTO(String.valueOf(year), revenue);
    }
}