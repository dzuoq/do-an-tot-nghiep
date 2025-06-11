package com.example.computershop.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    private String brandName;
    private String image;
    private String description;
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(updatable = false) // Không cho phép cập nhật, chỉ khởi tạo một lần khi tạo mới
    private LocalDateTime createdAt;

    // Constructors
    public Brand() {
    }

    public Brand(Long brandId) {
        this.brandId = brandId;
    }

    public Brand(Long brandId, String brandName, String image) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.image = image;
    }

    public Brand(Long brandId, String brandName, String image, String description, Boolean isDelete,
            LocalDateTime createdAt) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.image = image;
        this.description = description;
        this.isDelete = isDelete;
        this.createdAt = createdAt; // Khởi tạo createdAt
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt; // Getter cho createdAt
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
