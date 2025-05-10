package ptithcm.edu.pharmacy.service;

import ptithcm.edu.pharmacy.dto.OverallSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.ProductSalesStatsDTO;
import ptithcm.edu.pharmacy.dto.RevenueByPeriodDTO; // Import the new DTO

import java.time.LocalDate;
import java.util.List;

public interface SalesStatisticsService {
    OverallSalesStatsDTO getOverallSalesStatistics(LocalDate startDate, LocalDate endDate);
    List<ProductSalesStatsDTO> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit);

    // New methods for time-based revenue
    List<RevenueByPeriodDTO> getRevenueByDay(LocalDate startDate, LocalDate endDate);
    List<RevenueByPeriodDTO> getRevenueByMonth(int year);
    List<RevenueByPeriodDTO> getRevenueBySpecificMonth(int year, int month); // For a single specific month
    List<RevenueByPeriodDTO> getRevenueByQuarter(int year);
    List<RevenueByPeriodDTO> getRevenueBySpecificQuarter(int year, int quarter); // For a single specific quarter
    List<RevenueByPeriodDTO> getRevenueByYear(List<Integer> years);
    RevenueByPeriodDTO getRevenueForSpecificYear(int year); // For a single specific year
}