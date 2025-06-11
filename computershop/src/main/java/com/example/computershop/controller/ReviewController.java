package com.example.computershop.controller;

import com.example.computershop.dto.ReviewDTO;
import com.example.computershop.model.Review;
import com.example.computershop.service.ReviewService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // API lấy tất cả các review với phân trang và tìm kiếm
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ReviewDTO>>> getAllReviews(
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        PaginationResponse<ReviewDTO> reviews = reviewService.getReviewsWithPagination(page - 1, limit, searchText);
        ApiResponse<PaginationResponse<ReviewDTO>> response = new ApiResponse<>(200, reviews,
                "Lấy đánh giá thành công");
        return ResponseEntity.ok(response);
    }

    // API lấy các review của một sản phẩm cụ thể với phân trang và tìm kiếm
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PaginationResponse<ReviewDTO>>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchText) {

        PaginationResponse<ReviewDTO> reviews = reviewService.getReviewsByProductWithPagination(productId, page - 1,
                size, searchText);
        ApiResponse<PaginationResponse<ReviewDTO>> response = new ApiResponse<>(200, reviews,
                "Lấy đánh giá của sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@RequestBody Map<String, Object> reviewData) {
        try {
            Long productId = Long.valueOf(reviewData.get("productId").toString());
            Long userId = Long.valueOf(reviewData.get("userId").toString());
            String reviewText = reviewData.get("reviewText").toString();
            int rating = Integer.parseInt(reviewData.get("rating").toString());

            // Lấy thời gian hiện tại cho reviewDate
            LocalDateTime reviewDate = LocalDateTime.now();

            Review createdReview = reviewService.createReview(productId, userId, reviewText, rating, reviewDate);
            ReviewDTO createdReviewDTO = reviewService.convertToDTO(createdReview);

            ApiResponse<ReviewDTO> response = new ApiResponse<>(201, createdReviewDTO, "Tạo đánh giá thành công");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            ApiResponse<ReviewDTO> response = new ApiResponse<>(500, null, "Lỗi khi tạo đánh giá");
            return ResponseEntity.status(500).body(response);
        }
    }
}
