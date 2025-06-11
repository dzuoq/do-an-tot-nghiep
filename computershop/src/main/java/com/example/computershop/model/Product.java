package com.example.computershop.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private BigDecimal price;
    @Column(columnDefinition = "LONGTEXT") // Đảm bảo rằng Hibernate sẽ tạo cột là TEXT trong database
    private String description;

    private BigDecimal discount;
    private String badge;
    private int stock;
    private Boolean isNewProduct;
    private Boolean isSale;
    private Boolean isSpecial;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brand brand;

    private Boolean isDelete = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariation> variations;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    // Constructors, Getters, and Setters

    public Product() {
    }

    public Product(String productName, BigDecimal price, String description, BigDecimal discount, String badge,
            int stock, Boolean isNewProduct, Boolean isSale, Boolean isSpecial, Category category, Brand brand) {
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
    }

    public Product(Long productId2) {
        // TODO Auto-generated constructor stub
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public List<ProductVariation> getVariations() {
        return variations;
    }

    public void setVariations(List<ProductVariation> variations) {
        this.variations = variations;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public ProductImage getDefaultImage() {
        if (images != null) {
            for (ProductImage image : images) {
                if (Boolean.TRUE.equals(image.getIsDefault())) {
                    return image;
                }
            }
        }
        return null; // Nếu không có hình ảnh nào là mặc định
    }
}
