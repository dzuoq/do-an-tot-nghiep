package com.example.computershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.computershop.repository.OrderRepository;
import com.example.computershop.repository.ProductRepository;
import com.example.computershop.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RevenueService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy doanh thu của hôm nay
    public BigDecimal getTodayRevenue() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);

        BigDecimal todayRevenue = orderRepository.getTotalRevenueForDay(startOfToday, endOfToday);

        // Nếu todayRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return todayRevenue != null ? todayRevenue : BigDecimal.ZERO;
    }

    // Lấy doanh thu của ngày hôm qua
    public BigDecimal getYesterdayRevenue() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = startOfYesterday.plusDays(1);

        BigDecimal yesterdayRevenue = orderRepository.getTotalRevenueForDay(startOfYesterday, endOfYesterday);

        // Nếu yesterdayRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return yesterdayRevenue != null ? yesterdayRevenue : BigDecimal.ZERO;
    }

    // Lấy doanh thu của tháng hiện tại
    public BigDecimal getMonthRevenue() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().plusDays(1).atStartOfDay();

        BigDecimal monthRevenue = orderRepository.getTotalRevenueForMonth(startOfMonth, endOfToday);

        // Nếu monthRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return monthRevenue != null ? monthRevenue : BigDecimal.ZERO;
    }

    // Lấy doanh thu của tháng trước
    public BigDecimal getLastMonthRevenue() {
        LocalDateTime startOfLastMonth = LocalDate.now().withDayOfMonth(1).minusMonths(1).atStartOfDay();
        LocalDateTime startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal lastMonthRevenue = orderRepository.getTotalRevenueForMonth(startOfLastMonth, startOfThisMonth);

        // Nếu lastMonthRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return lastMonthRevenue != null ? lastMonthRevenue : BigDecimal.ZERO;
    }

    // Lấy doanh thu của năm hiện tại
    public BigDecimal getYearlyRevenue() {
        LocalDateTime startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().plusDays(1).atStartOfDay();

        BigDecimal yearlyRevenue = orderRepository.getTotalRevenueForYear(startOfYear, endOfToday);

        // Nếu yearlyRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return yearlyRevenue != null ? yearlyRevenue : BigDecimal.ZERO;
    }

    // Lấy doanh thu của năm trước
    public BigDecimal getLastYearRevenue() {
        LocalDateTime startOfLastYear = LocalDate.now().withDayOfYear(1).minusYears(1).atStartOfDay();
        LocalDateTime startOfThisYear = LocalDate.now().withDayOfYear(1).atStartOfDay();

        BigDecimal lastYearRevenue = orderRepository.getTotalRevenueForYear(startOfLastYear, startOfThisYear);

        // Nếu lastYearRevenue là null, đặt giá trị mặc định là BigDecimal.ZERO
        return lastYearRevenue != null ? lastYearRevenue : BigDecimal.ZERO;
    }

    // Lấy tổng số sản phẩm (isDelete = false)
    public Long getTotalProducts() {
        return productRepository.getCountActiveProduct();
    }

    // Lấy tổng số đơn hàng
    public Long getTotalOrders() {
        return orderRepository.countOrders();
    }

    // Lấy tổng số người dùng (isDelete = false)
    public Long getTotalUsers() {
        return userRepository.countActiveUsers();
    }

    public List<Map<String, Object>> getDailyOrderAndRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = orderRepository.getDailyOrderAndRevenue(startDate, endDate);

        // Tạo danh sách kết quả để trả về cho API
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", result[0].toString()); // Ngày
            dayData.put("orderCount", result[1]); // Số đơn hàng
            dayData.put("totalRevenue", result[2]); // Doanh thu

            data.add(dayData);
        }

        return data;
    }
}
