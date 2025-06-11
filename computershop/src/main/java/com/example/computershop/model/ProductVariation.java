package com.example.computershop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "ProductVariation")
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variationId;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    private String attributeName; // Ví dụ: "Color", "Storage", "RAM"
    private String attributeValue; // Ví dụ: "Red", "128GB", "16GB"

    private BigDecimal price; // Giá cho từng biến thể sản phẩm
    private int quantity; // Số lượng cho từng biến thể sản phẩm

    private Boolean isDelete = false; // Trường để quản lý trạng thái đã xóa hay chưa

    // Constructors
    public ProductVariation() {
    }

    public ProductVariation(Product product, String attributeName, String attributeValue, BigDecimal price,
            int quantity) {
        this.product = product;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.price = price;
        this.quantity = quantity;
        this.isDelete = false; // Mặc định là false khi tạo mới
    }

    // Getters and Setters
    public Long getVariationId() {
        return variationId;
    }

    public void setVariationId(Long variationId) {
        this.variationId = variationId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}
