package com.example.computershop.repository;

import com.example.computershop.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Tìm kiếm các review dựa trên nội dung review
    Page<Review> findByReviewTextContaining(String reviewText, Pageable pageable);

    // Tìm các review theo productId và nội dung review
    Page<Review> findByProduct_ProductIdAndReviewTextContaining(Long productId, String reviewText, Pageable pageable);

    // Tìm các review chỉ theo productId (nếu không có từ khóa tìm kiếm)
    Page<Review> findByProduct_ProductId(Long productId, Pageable pageable);

    // Tìm kiếm review dựa trên nội dung review, tên người dùng hoặc số điện thoại
    Page<Review> findByReviewTextContainingOrUser_FullNameContainingOrUser_PhoneNumberContaining(
            String reviewText,
            String fullName,
            String phoneNumber,
            Pageable pageable);

    // Tính trung bình rating cho productId
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double calculateAvgRatingForProduct(@Param("productId") Long productId);

    // Đếm số lượng review cho productId
    int countByProduct_ProductId(Long productId);
}
