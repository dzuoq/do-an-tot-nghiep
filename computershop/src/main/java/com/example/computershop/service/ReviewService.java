package com.example.computershop.service;

import com.example.computershop.dto.ReviewDTO;
import com.example.computershop.model.Product;
import com.example.computershop.model.Review;
import com.example.computershop.model.User;
import com.example.computershop.repository.ProductRepository;
import com.example.computershop.repository.ReviewRepository;
import com.example.computershop.repository.UserRepository;
import com.example.computershop.utils.PaginationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public PaginationResponse<ReviewDTO> getReviewsWithPagination(int page, int size, String searchText) {
        // Tạo Pageable với thông tin sắp xếp theo reviewDate giảm dần (DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewDate"));
        Page<Review> reviewPage;

        if (searchText != null && !searchText.trim().isEmpty()) {
            // Tìm kiếm theo reviewText, fullName hoặc phoneNumber
            reviewPage = reviewRepository
                    .findByReviewTextContainingOrUser_FullNameContainingOrUser_PhoneNumberContaining(
                            searchText.trim(),
                            searchText.trim(),
                            searchText.trim(),
                            pageable);
        } else {
            // Nếu không có searchText thì chỉ phân trang và sắp xếp theo reviewDate
            reviewPage = reviewRepository.findAll(pageable);
        }

        // Chuyển đổi danh sách Review sang ReviewDTO
        List<ReviewDTO> reviewDTOs = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Tạo đối tượng PaginationResponse để trả về dữ liệu cùng với thông tin phân
        // trang
        PaginationResponse<ReviewDTO> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(reviewDTOs);
        paginationResponse.setPage(reviewPage.getNumber() + 1); // Hiển thị page từ 1 (1-based index)
        paginationResponse.setLimit(reviewPage.getSize());
        paginationResponse.setTotalElements(reviewPage.getTotalElements());
        paginationResponse.setTotalPages(reviewPage.getTotalPages());

        return paginationResponse;
    }

    // Lấy các review của một sản phẩm cụ thể với phân trang và tìm kiếm
    public PaginationResponse<ReviewDTO> getReviewsByProductWithPagination(Long productId, int page, int size,
            String searchText) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage;

        if (searchText != null && !searchText.trim().isEmpty()) {
            reviewPage = reviewRepository.findByProduct_ProductIdAndReviewTextContaining(productId, searchText.trim(),
                    pageable);
        } else {
            reviewPage = reviewRepository.findByProduct_ProductId(productId, pageable);
        }

        List<ReviewDTO> reviewDTOs = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PaginationResponse<ReviewDTO> paginationResponse = new PaginationResponse<>();
        paginationResponse.setContent(reviewDTOs);
        paginationResponse.setPage(reviewPage.getNumber() + 1);
        paginationResponse.setLimit(reviewPage.getSize());
        paginationResponse.setTotalElements(reviewPage.getTotalElements());
        paginationResponse.setTotalPages(reviewPage.getTotalPages());

        return paginationResponse;
    }

    // Phương thức để chuyển đổi từ Review sang ReviewDTO
    // Sửa đổi mức truy cập từ private sang public
    public ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getReviewId(),
                review.getProduct().getProductName(), // Lấy tên sản phẩm
                review.getUser().getFullName(), // Lấy tên đầy đủ của người dùng
                review.getUser().getPhoneNumber(), // Lấy số điện thoại của người dùng
                review.getReviewText(),
                review.getRating(),
                review.getReviewDate());
    }

    // Tạo mới một đánh giá
    public Review createReview(Long productId, Long userId, String reviewText, int rating, LocalDateTime reviewDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setReviewDate(reviewDate);

        return reviewRepository.save(review);
    }
}
