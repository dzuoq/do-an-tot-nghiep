package com.example.computershop.service;

import com.example.computershop.model.Category;
import com.example.computershop.repository.CategoryRepository;
import com.example.computershop.utils.FirebaseImageUploadService;
import com.example.computershop.utils.PaginationResponse; // Cấu trúc tùy chỉnh cho phản hồi phân trang
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService;

    // Lấy tất cả các danh mục chưa bị xóa với phân trang, tìm kiếm và sắp xếp
    public PaginationResponse<Category> getAllCategories(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Category> categoryPage;

        // Tìm kiếm theo từ khóa nếu có
        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = categoryRepository.findByCategoryNameContainingAndIsDeleteFalse(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findByIsDeleteFalse(pageable);
        }

        // Tạo đối tượng phản hồi tùy chỉnh
        PaginationResponse<Category> response = new PaginationResponse<>();
        response.setContent(categoryPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());

        return response;
    }

    // Lấy danh mục theo ID
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).filter(category -> !category.getIsDelete());
    }

    // Thêm hoặc cập nhật danh mục với việc tải ảnh lên Firebase
    public Category saveCategory(Category category, MultipartFile imageFile) throws IOException {
        // Nếu có tệp ảnh được cung cấp
        if (imageFile != null && !imageFile.isEmpty()) {
            // Tải ảnh lên Firebase và lưu URL vào category
            String imageUrl = firebaseImageUploadService.uploadImage(imageFile);
            category.setImage(imageUrl);
        }

        // Lưu danh mục vào cơ sở dữ liệu
        return categoryRepository.save(category);
    }

    // Xóa danh mục bằng cách cập nhật isDelete thành true
    public void deleteCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        category.ifPresent(c -> {
            c.setIsDelete(true);
            categoryRepository.save(c);
        });
    }
}
