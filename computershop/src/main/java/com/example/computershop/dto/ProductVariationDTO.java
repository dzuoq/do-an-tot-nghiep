package com.example.computershop.dto;

import java.math.BigDecimal;

public class ProductVariationDTO {
    private Long variationId;
    private String attributeName;
    private String attributeValue;
    private BigDecimal price;
    private int quantity;

    public ProductVariationDTO() {
    }

    public ProductVariationDTO(Long variationId, String attributeName, String attributeValue, BigDecimal price,
            int quantity) {
        this.variationId = variationId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getVariationId() {
        return variationId;
    }

    public void setVariationId(Long variationId) {
        this.variationId = variationId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Constructors, Getters, and Setters
}
