package com.example.computershop.controller;

import com.example.computershop.model.Voucher;
import com.example.computershop.service.VoucherService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // Lấy tất cả các voucher với phân trang, tìm kiếm và sắp xếp
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<Voucher>>> getAllVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PaginationResponse<Voucher> vouchers = voucherService.getAllVouchers(keyword, page, limit);
        ApiResponse<PaginationResponse<Voucher>> response = new ApiResponse<>(200, vouchers,
                "Lấy danh sách voucher thành công");
        return ResponseEntity.ok(response);
    }

    // Tạo voucher mới
    @PostMapping
    public ResponseEntity<ApiResponse<Voucher>> createVoucher(@RequestBody Voucher voucher) {
        Voucher createdVoucher = voucherService.createVoucher(voucher);
        ApiResponse<Voucher> response = new ApiResponse<>(201, createdVoucher, "Tạo voucher thành công");
        return ResponseEntity.status(201).body(response);
    }

    // Cập nhật voucher
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Voucher>> updateVoucher(@PathVariable Long id, @RequestBody Voucher voucher) {
        Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);
        ApiResponse<Voucher> response = new ApiResponse<>(200, updatedVoucher, "Cập nhật voucher thành công");
        return ResponseEntity.ok(response);
    }

    // Xóa voucher
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa voucher thành công");
        return ResponseEntity.ok(response);
    }

    // Đánh dấu voucher đã sử dụng
    @PatchMapping("/{id}/mark-as-used")
    public ResponseEntity<ApiResponse<Void>> markAsUsed(@PathVariable Long id) {
        voucherService.markAsUsed(id);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Đánh dấu voucher là đã sử dụng thành công");
        return ResponseEntity.ok(response);
    }

    // Kiểm tra voucher
    @GetMapping("/check/{code}")
    public ResponseEntity<ApiResponse<Voucher>> checkVoucher(@PathVariable String code) {
        Optional<Voucher> voucher = voucherService.checkVoucher(code);
        return voucher.map(v -> {
            ApiResponse<Voucher> response = new ApiResponse<>(200, v, "Voucher hợp lệ");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<Voucher> response = new ApiResponse<>(404, null, "Voucher không hợp lệ");
            return ResponseEntity.status(404).body(response);
        });
    }

    // Thay đổi trạng thái voucher
    @PatchMapping("/{id}/change-status")
    public ResponseEntity<ApiResponse<Void>> changeStatusVoucher(@PathVariable Long id) {
        voucherService.changeStatusVoucher(id);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Thay đổi trạng thái voucher thành công");
        return ResponseEntity.ok(response);
    }
}
