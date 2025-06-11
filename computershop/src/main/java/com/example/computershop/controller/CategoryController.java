package com.example.computershop.controller;

import com.example.computershop.model.Category;
import com.example.computershop.service.CategoryService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả các danh mục với phân trang, tìm kiếm và sắp xếp
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<Category>>> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page, // Chỉnh về page 1
            @RequestParam(defaultValue = "10") int limit) { // Đổi từ size thành limit
        PaginationResponse<Category> categories = categoryService.getAllCategories(keyword, page, limit);
        ApiResponse<PaginationResponse<Category>> response = new ApiResponse<>(200, categories,
                "Lấy danh mục thành công");
        return ResponseEntity.ok(response);
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(cat -> {
            ApiResponse<Category> response = new ApiResponse<>(200, cat, "Lấy danh mục thành công");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<Category> response = new ApiResponse<>(404, null, "Không tìm thấy danh mục");
            return ResponseEntity.status(404).body(response);
        });
    }

    // Thêm danh mục mới và tải ảnh lên Firebase
    // Thêm danh mục mới và tải ảnh lên Firebase
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @RequestParam("categoryName") String categoryName,
            @RequestParam("description") String description,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            // Tạo đối tượng Category mới
            Category category = new Category();
            category.setCategoryName(categoryName);
            category.setDescription(description);
            category.setIsDelete(false); // Mặc định isDelete là false

            // Lưu danh mục và ảnh
            Category savedCategory = categoryService.saveCategory(category, imageFile);
            ApiResponse<Category> response = new ApiResponse<>(201, savedCategory, "Tạo danh mục thành công");
            return ResponseEntity.status(201).body(response);
        } catch (IOException e) {
            ApiResponse<Category> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("description") String description,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Optional<Category> categoryOptional = categoryService.getCategoryById(id);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                category.setCategoryName(categoryName);
                category.setDescription(description);

                // Lưu cập nhật danh mục và (nếu có) cập nhật ảnh
                Category savedCategory = categoryService.saveCategory(category, imageFile);
                ApiResponse<Category> response = new ApiResponse<>(200, savedCategory, "Cập nhật danh mục thành công");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Category> response = new ApiResponse<>(404, null, "Không tìm thấy danh mục");
                return ResponseEntity.status(404).body(response);
            }
        } catch (IOException e) {
            ApiResponse<Category> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Xóa danh mục (Chuyển isDelete thành true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (categoryOptional.isPresent()) {
            categoryService.deleteCategory(id);
            ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa danh mục thành công");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy danh mục");
            return ResponseEntity.status(404).body(response);
        }
    }
}
