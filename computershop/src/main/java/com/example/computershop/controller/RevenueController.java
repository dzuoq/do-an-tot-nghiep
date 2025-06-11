package com.example.computershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.computershop.service.RevenueService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

        @Autowired
        private RevenueService revenueService;

        @GetMapping("/summary")
        public Map<String, Object> getRevenueSummary() {
                Map<String, Object> response = new HashMap<>();

                BigDecimal todayRevenue = revenueService.getTodayRevenue();
                BigDecimal yesterdayRevenue = revenueService.getYesterdayRevenue();
                BigDecimal monthlyRevenue = revenueService.getMonthRevenue();
                BigDecimal lastMonthRevenue = revenueService.getLastMonthRevenue();
                BigDecimal yearlyRevenue = revenueService.getYearlyRevenue();
                BigDecimal lastYearRevenue = revenueService.getLastYearRevenue();

                Long totalProducts = revenueService.getTotalProducts();
                Long totalOrders = revenueService.getTotalOrders();
                Long totalUsers = revenueService.getTotalUsers();

                // Tính toán phần trăm tăng trưởng
                BigDecimal todayIncreasePercentage = yesterdayRevenue.compareTo(BigDecimal.ZERO) > 0
                                ? todayRevenue.subtract(yesterdayRevenue).multiply(BigDecimal.valueOf(100)).divide(
                                                yesterdayRevenue, 2,
                                                BigDecimal.ROUND_HALF_UP)
                                : BigDecimal.ZERO;

                BigDecimal monthlyIncreasePercentage = lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0
                                ? monthlyRevenue.subtract(lastMonthRevenue).multiply(BigDecimal.valueOf(100)).divide(
                                                lastMonthRevenue,
                                                2, BigDecimal.ROUND_HALF_UP)
                                : BigDecimal.ZERO;

                BigDecimal yearlyIncreasePercentage = lastYearRevenue.compareTo(BigDecimal.ZERO) > 0
                                ? yearlyRevenue.subtract(lastYearRevenue).multiply(BigDecimal.valueOf(100)).divide(
                                                lastYearRevenue, 2,
                                                BigDecimal.ROUND_HALF_UP)
                                : BigDecimal.ZERO;

                // Thêm dữ liệu vào response
                response.put("todayRevenue", todayRevenue);
                response.put("yesterdayRevenue", yesterdayRevenue);
                response.put("todayIncreasePercentage", todayIncreasePercentage);

                response.put("monthlyRevenue", monthlyRevenue);
                response.put("lastMonthRevenue", lastMonthRevenue);
                response.put("monthlyIncreasePercentage", monthlyIncreasePercentage);

                response.put("yearlyRevenue", yearlyRevenue);
                response.put("lastYearRevenue", lastYearRevenue);
                response.put("yearlyIncreasePercentage", yearlyIncreasePercentage);

                response.put("totalProducts", totalProducts);
                response.put("totalOrders", totalOrders);
                response.put("totalUsers", totalUsers);

                return response;
        }

        @GetMapping("/daily")
        public ResponseEntity<?> getDailyOrderAndRevenue(
                        @RequestParam String startDate,
                        @RequestParam String endDate) {

                // Chuyển đổi chuỗi startDate và endDate sang LocalDateTime
                LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
                LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

                List<Map<String, Object>> data = revenueService.getDailyOrderAndRevenue(start, end);

                // Tạo cấu trúc dữ liệu giống mẫu yêu cầu
                Map<String, Object> response = new HashMap<>();
                List<String> categories = new ArrayList<>();
                List<Long> orderCounts = new ArrayList<>(); // Thay đổi từ Integer sang Long
                List<BigDecimal> revenues = new ArrayList<>();

                for (Map<String, Object> dayData : data) {
                        categories.add(dayData.get("date").toString());
                        orderCounts.add((Long) dayData.get("orderCount")); // Sử dụng Long
                        revenues.add((BigDecimal) dayData.get("totalRevenue"));
                }

                response.put("series", Arrays.asList(
                                Map.of("name", "Số đơn hàng", "data", orderCounts),
                                Map.of("name", "Doanh thu", "data", revenues)));
                response.put("categories", categories);

                return ResponseEntity.ok(response);
        }
}
