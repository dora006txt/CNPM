package ptithcm.edu.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ptithcm.edu.pharmacy.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find orders by user ID
    List<Order> findByUser_UserId(Integer userId);

    // Find an order by its unique code
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Long countByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // --- New Query Methods for Time-Based Revenue ---

    // Revenue by Day
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS orderDay, SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY orderDay " +
           "ORDER BY orderDay ASC")
    List<Object[]> findRevenueByDayBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Revenue by Month for a given Year
    @Query("SELECT YEAR(o.orderDate) AS orderYear, MONTH(o.orderDate) AS orderMonth, SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year " +
           "GROUP BY orderYear, orderMonth " +
           "ORDER BY orderMonth ASC")
    List<Object[]> findRevenueByMonthForYear(@Param("year") int year);

    // Revenue for a specific Month and Year
    @Query("SELECT SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    BigDecimal findRevenueForSpecificMonth(@Param("year") int year, @Param("month") int month);

    // Revenue by Quarter for a given Year (Using native query for QUARTER function if JPQL doesn't directly support it well across all JPA providers for H2/MySQL)
    // For MySQL, QUARTER() function is standard.
    @Query("SELECT YEAR(o.orderDate) AS orderYear, QUARTER(o.orderDate) AS orderQuarter, SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year " +
           "GROUP BY orderYear, orderQuarter " +
           "ORDER BY orderQuarter ASC")
    List<Object[]> findRevenueByQuarterForYear(@Param("year") int year);
    
    // Revenue for a specific Quarter and Year
    @Query("SELECT SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year AND QUARTER(o.orderDate) = :quarter")
    BigDecimal findRevenueForSpecificQuarter(@Param("year") int year, @Param("quarter") int quarter);

    // Revenue by Year for a list of Years
    @Query("SELECT YEAR(o.orderDate) AS orderYear, SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) IN :years " +
           "GROUP BY orderYear " +
           "ORDER BY orderYear ASC")
    List<Object[]> findRevenueByYearIn(@Param("years") List<Integer> years);

    // Revenue for a specific Year
    @Query("SELECT SUM(o.finalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = :year")
    BigDecimal findRevenueForSpecificYear(@Param("year") int year);
}