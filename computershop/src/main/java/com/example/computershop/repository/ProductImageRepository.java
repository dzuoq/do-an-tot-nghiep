package com.example.computershop.repository;

import com.example.computershop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // Lấy tất cả hình ảnh của một sản phẩm
    List<ProductImage> findByProduct_ProductId(Long productId);

    // Có thể thêm các phương thức khác nếu cần, ví dụ:
    // Xóa tất cả hình ảnh theo productId
    void deleteByProduct_ProductId(Long productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isDefault = false WHERE pi.product.productId = :productId AND pi.isDelete = false")
    void resetDefaultForProduct(Long productId);
}
