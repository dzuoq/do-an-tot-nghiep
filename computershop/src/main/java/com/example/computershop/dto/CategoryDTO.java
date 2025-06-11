package com.example.computershop.dto;

public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private String image; // Thêm trường hình ảnh nếu cần

    // Constructors
    public CategoryDTO() {}

    public CategoryDTO(Long categoryId, String categoryName, String image) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
