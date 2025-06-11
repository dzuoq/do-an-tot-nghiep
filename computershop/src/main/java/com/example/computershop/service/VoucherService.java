package com.example.computershop.service;

import com.example.computershop.model.Voucher;
import com.example.computershop.repository.VoucherRepository;
import com.example.computershop.utils.PaginationResponse; // Cấu trúc tùy chỉnh cho phản hồi phân trang
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    // Lấy tất cả các voucher với phân trang
    public PaginationResponse<Voucher> getAllVouchers(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("expirationDate").descending());
        Page<Voucher> voucherPage;

        // Tìm kiếm theo từ khóa nếu có
        if (keyword != null && !keyword.isEmpty()) {
            voucherPage = voucherRepository.findByCodeContaining(keyword, pageable);
        } else {
            voucherPage = voucherRepository.findAll(pageable);
        }

        // Tạo đối tượng phản hồi tùy chỉnh
        PaginationResponse<Voucher> response = new PaginationResponse<>();
        response.setContent(voucherPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(voucherPage.getTotalElements());
        response.setTotalPages(voucherPage.getTotalPages());

        return response;
    }

    // Tạo voucher mới
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    // Cập nhật voucher
    public Voucher updateVoucher(Long id, Voucher voucher) {
        voucher.setVoucherId(id);
        return voucherRepository.save(voucher);
    }

    // Xóa voucher
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }

    // Đánh dấu voucher đã sử dụng
    public void markAsUsed(Long id) {
        Optional<Voucher> voucher = voucherRepository.findById(id);
        voucher.ifPresent(v -> {
            v.setIsUsed(true);
            voucherRepository.save(v);
        });
    }

    // Kiểm tra voucher
    public Optional<Voucher> checkVoucher(String code) {
        Optional<Voucher> optionalVoucher = voucherRepository.findByCode(code);

        // Kiểm tra nếu voucher tồn tại và chưa hết hạn
        if (optionalVoucher.isPresent()) {
            Voucher voucher = optionalVoucher.get();

            // Kiểm tra ngày hết hạn
            if (voucher.getExpirationDate().isAfter(LocalDateTime.now())) {
                return Optional.of(voucher);
            }
        }

        // Trả về Optional.empty() nếu voucher không tồn tại hoặc đã hết hạn
        return Optional.empty();
    }

    // Thay đổi trạng thái voucher
    public void changeStatusVoucher(Long id) {
        Optional<Voucher> voucher = voucherRepository.findById(id);
        voucher.ifPresent(v -> {
            // Chuyển đổi trạng thái voucher
            v.setIsUsed(!v.getIsUsed());
            voucherRepository.save(v);
        });
    }
}
