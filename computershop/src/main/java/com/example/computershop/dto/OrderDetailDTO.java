package com.example.computershop.dto;

import java.math.BigDecimal;

public class OrderDetailDTO {
    private Long orderDetailId;
    private String productName;
    private ProductImageDTO productImages;
    private int productQuantity;
    private BigDecimal price;
    private String variationName;

    public OrderDetailDTO() {
    }

    public OrderDetailDTO(Long orderDetailId, String productName, ProductImageDTO productImages, int productQuantity,
            BigDecimal price, String variationName) {
        this.orderDetailId = orderDetailId;
        this.productName = productName;
        this.productImages = productImages;
        this.productQuantity = productQuantity;
        this.price = price;
        this.variationName = variationName;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public ProductImageDTO getProductImages() {
        return productImages;
    }

    public void setProductImages(ProductImageDTO productImages) {
        this.productImages = productImages;
    }

    // Getters and setters...
}
