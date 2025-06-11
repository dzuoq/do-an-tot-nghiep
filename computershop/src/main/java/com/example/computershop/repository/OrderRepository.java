package com.example.computershop.repository;

import com.example.computershop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // Find orders by userId with pagination
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);

    // Find order by code
    Optional<Order> findByCode(String code);

    // Sửa kiểu dữ liệu của tham số từ LocalDate thành LocalDateTime và thêm điều
    // kiện trạng thái 'Đã giao hàng'
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.date >= :startOfDay AND o.date < :endOfDay AND o.status = 'Đã giao hàng'")
    BigDecimal getTotalRevenueForDay(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.date >= :startOfMonth AND o.date < :endOfMonth AND o.status = 'Đã giao hàng'")
    BigDecimal getTotalRevenueForMonth(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.date >= :startOfYear AND o.date < :endOfYear AND o.status = 'Đã giao hàng'")
    BigDecimal getTotalRevenueForYear(LocalDateTime startOfYear, LocalDateTime endOfYear);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'Đã giao hàng'")
    Long countOrders();

    @Query("SELECT DATE(o.date) as orderDate, COUNT(o) as orderCount, SUM(o.totalPrice) as totalRevenue "
            + "FROM Order o WHERE o.date >= :startDate AND o.date <= :endDate AND o.status = 'Đã giao hàng' "
            + "GROUP BY DATE(o.date) ORDER BY orderDate")
    List<Object[]> getDailyOrderAndRevenue(LocalDateTime startDate, LocalDateTime endDate);

}
