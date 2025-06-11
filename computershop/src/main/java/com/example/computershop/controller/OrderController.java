package com.example.computershop.controller;

import com.example.computershop.dto.OrderDTO;
import com.example.computershop.service.OrderService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import com.example.computershop.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

        @Autowired
        private OrderService orderService;

        // API to create a new order
        @PostMapping("/create")
        public ResponseEntity<Void> createOrder(@RequestBody Order orderRequest) throws Exception {
                // Gọi OrderService để tạo đơn hàng
                orderService.createOrder(orderRequest);

                // Trả về mã trạng thái thành công mà không có dữ liệu trả về
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @GetMapping("/all")
        public ResponseEntity<ApiResponse<PaginationResponse<OrderDTO>>> getAllOrders(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit,
                        @RequestParam(required = false) String code,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String method) {

                Pageable pageable = PageRequest.of(page - 1, limit);

                // Gọi đến service để lấy danh sách đơn hàng kèm các bộ lọc
                Page<Order> ordersPage = orderService.getAllOrders(pageable, code, status, method);

                List<OrderDTO> orderDTOList = ordersPage.getContent().stream()
                                .map(orderService::convertToDTO)
                                .collect(Collectors.toList());

                PaginationResponse<OrderDTO> paginationResponse = new PaginationResponse<>();
                paginationResponse.setContent(orderDTOList);
                paginationResponse.setPage(page);
                paginationResponse.setLimit(limit);
                paginationResponse.setTotalElements(ordersPage.getTotalElements());
                paginationResponse.setTotalPages(ordersPage.getTotalPages());

                ApiResponse<PaginationResponse<OrderDTO>> response = new ApiResponse<>(200, paginationResponse,
                                "Lấy danh sách đơn hàng thành công");
                return ResponseEntity.ok(response);
        }

        // API to get orders by user ID with pagination
        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<PaginationResponse<OrderDTO>>> getOrdersByUserId(
                        @PathVariable Long userId,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {
                Pageable pageable = PageRequest.of(page - 1, limit);
                Page<Order> userOrdersPage = orderService.getOrdersByUserId(userId, pageable);

                List<OrderDTO> orderDTOList = userOrdersPage.getContent().stream()
                                .map(orderService::convertToDTO)
                                .collect(Collectors.toList());

                PaginationResponse<OrderDTO> paginationResponse = new PaginationResponse<>();
                paginationResponse.setContent(orderDTOList);
                paginationResponse.setPage(page);
                paginationResponse.setLimit(limit);
                paginationResponse.setTotalElements(userOrdersPage.getTotalElements());
                paginationResponse.setTotalPages(userOrdersPage.getTotalPages());

                ApiResponse<PaginationResponse<OrderDTO>> response = new ApiResponse<>(200, paginationResponse,
                                "Lấy danh sách đơn hàng theo người dùng thành công");
                return ResponseEntity.ok(response);
        }

        // API to change order status
        @PutMapping("/{orderId}/status")
        public ResponseEntity<ApiResponse<Order>> changeOrderStatus(@PathVariable Long orderId,
                        @RequestParam String status) {
                orderService.changeOrderStatus(orderId, status);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}
