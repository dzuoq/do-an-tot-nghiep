package com.example.computershop.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long reviewId;
    private String productName; // Tên sản phẩm
    private String fullName; // Tên đầy đủ của người dùng
    private String phoneNumber; // Số điện thoại của người dùng
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;

    // Constructor
    public ReviewDTO(Long reviewId, String productName, String fullName, String phoneNumber, String reviewText,
            int rating, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.productName = productName;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = reviewDate;
    }

    // Getters và Setters
    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}
