package com.example.computershop.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String description;
    private BigDecimal discount;
    private String badge;
    private int stock;
    private Boolean isNewProduct;
    private Boolean isSale;
    private Boolean isSpecial;
    private CategoryDTO category; // DTO cho category
    private BrandDTO brand; // DTO cho brand
    private List<ProductVariationDTO> variations; // Danh sách biến thể
    private List<ProductImageDTO> images; // Danh sách hình ảnh
    private Double avgRating;
    private Integer reviewCount;
    private Boolean isDelete;

    public ProductDTO() {
    }

    public ProductDTO(Long productId, String productName, BigDecimal price, String description, BigDecimal discount,
            String badge, int stock, Boolean isNewProduct, Boolean isSale, Boolean isSpecial, CategoryDTO category,
            BrandDTO brand, List<ProductVariationDTO> variations, List<ProductImageDTO> images, Double avgRating,
            Integer reviewCount, Boolean isDelete) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.discount = discount;
        this.badge = badge;
        this.stock = stock;
        this.isNewProduct = isNewProduct;
        this.isSale = isSale;
        this.isSpecial = isSpecial;
        this.category = category;
        this.brand = brand;
        this.variations = variations;
        this.images = images;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.isDelete = isDelete;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Boolean getIsNewProduct() {
        return isNewProduct;
    }

    public void setIsNewProduct(Boolean isNewProduct) {
        this.isNewProduct = isNewProduct;
    }

    public Boolean getIsSale() {
        return isSale;
    }

    public void setIsSale(Boolean isSale) {
        this.isSale = isSale;
    }

    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public BrandDTO getBrand() {
        return brand;
    }

    public void setBrand(BrandDTO brand) {
        this.brand = brand;
    }

    public List<ProductVariationDTO> getVariations() {
        return variations;
    }

    public void setVariations(List<ProductVariationDTO> variations) {
        this.variations = variations;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDTO> images) {
        this.images = images;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    // Constructors, Getters, and Setters
}
