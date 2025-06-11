package com.example.computershop.repository;

import com.example.computershop.model.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    // Lấy tất cả biến thể của một sản phẩm
    List<ProductVariation> findByProduct_ProductId(Long productId);
}
