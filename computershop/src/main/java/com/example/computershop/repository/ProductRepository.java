package com.example.computershop.repository;

import com.example.computershop.model.Product;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Các phương thức khác

    // Tìm sản phẩm theo tên không bao gồm những sản phẩm đã bị xóa
    Page<Product> findByProductNameContainingAndIsDeleteFalse(String productName, Pageable pageable);

    // Tìm sản phẩm theo categoryId không bao gồm những sản phẩm đã bị xóa
    Page<Product> findByCategory_CategoryIdAndIsDeleteFalse(Long categoryId, Pageable pageable);

    // Tìm sản phẩm theo brandId không bao gồm những sản phẩm đã bị xóa
    Page<Product> findByBrand_BrandIdAndIsDeleteFalse(Long brandId, Pageable pageable);

    // Tìm tất cả sản phẩm chưa bị xóa
    Page<Product> findByIsDeleteFalse(Pageable pageable);

    // Đếm tổng số sản phẩm chưa bị xóa
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isDelete = false")
    Long getCountActiveProduct();
        @Query("SELECT p FROM Product p WHERE p.category.categoryId IN :categoryIds OR p.brand.brandId IN :brandIds")
    List<Product> findRelatedProducts(@Param("categoryIds") Set<Long> categoryIds,
            @Param("brandIds") Set<Long> brandIds,
            Pageable pageable);

    // Lấy 10 sản phẩm ngẫu nhiên bằng ORDER BY RAND()
    @Query("SELECT p FROM Product p ORDER BY FUNCTION('RAND')")
    List<Product> findRandomProducts(Pageable pageable);
}
