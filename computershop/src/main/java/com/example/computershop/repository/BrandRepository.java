package com.example.computershop.repository;

import com.example.computershop.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    // Tìm kiếm thương hiệu theo tên và lọc các thương hiệu chưa bị xóa
    Page<Brand> findByBrandNameContainingAndIsDeleteFalse(String keyword, Pageable pageable);

    // Lấy tất cả các thương hiệu chưa bị xóa với phân trang
    Page<Brand> findByIsDeleteFalse(Pageable pageable);
}
