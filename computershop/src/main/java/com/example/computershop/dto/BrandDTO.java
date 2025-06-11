package com.example.computershop.dto;

public class BrandDTO {
    private Long brandId;
    private String brandName;
    private String image; // Thêm trường hình ảnh nếu cần

    // Constructors
    public BrandDTO() {
    }

    public BrandDTO(Long brandId, String brandName, String image) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.image = image;
    }

    // Getters and Setters
    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
