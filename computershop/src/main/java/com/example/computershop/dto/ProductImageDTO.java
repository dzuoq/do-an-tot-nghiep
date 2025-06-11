package com.example.computershop.dto;

public class ProductImageDTO {
    private Long imageId;
    private String imageUrl;
    private Boolean isDefault; // Thêm trường isDefault

    public ProductImageDTO() {
    }

    public ProductImageDTO(Long imageId, String imageUrl, Boolean isDefault) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.isDefault = isDefault; // Khởi tạo trường isDefault
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsDefault() {
        return isDefault; // Phương thức lấy giá trị isDefault
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault; // Phương thức thiết lập giá trị isDefault
    }
}
