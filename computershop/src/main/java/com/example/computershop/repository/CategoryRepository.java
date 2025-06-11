package com.example.computershop.repository;

import com.example.computershop.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Tìm kiếm danh mục theo tên và lọc các danh mục chưa bị xóa
    Page<Category> findByCategoryNameContainingAndIsDeleteFalse(String keyword, Pageable pageable);
    
    // Lấy tất cả các danh mục chưa bị xóa với phân trang
    Page<Category> findByIsDeleteFalse(Pageable pageable);
}
