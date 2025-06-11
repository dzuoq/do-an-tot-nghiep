package com.example.computershop.controller;

import com.example.computershop.model.New;
import com.example.computershop.service.NewService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewController {

    @Autowired
    private NewService newService;

    // Lấy tất cả các tin tức với phân trang, tìm kiếm và sắp xếp
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<New>>> getAllNews(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PaginationResponse<New> news = newService.getAllNews(keyword, page, limit);
        ApiResponse<PaginationResponse<New>> response = new ApiResponse<>(200, news, "Lấy tin tức thành công");
        return ResponseEntity.ok(response);
    }

    // Lấy tin tức theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<New>> getNewById(@PathVariable Long id) {
        Optional<New> news = newService.getNewById(id);
        return news.map(n -> {
            ApiResponse<New> response = new ApiResponse<>(200, n, "Lấy tin tức thành công");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<New> response = new ApiResponse<>(404, null, "Không tìm thấy tin tức");
            return ResponseEntity.status(404).body(response);
        });
    }

    // Thêm tin tức mới
    @PostMapping
    public ResponseEntity<ApiResponse<New>> createNew(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        New newObj = new New();
        newObj.setTitle(title);
        newObj.setContent(content);
        newObj.setIsDeleted(false); // Mặc định isDeleted là false

        try {
            New savedNew = newService.saveNew(newObj, imageFile); // Gọi service với hình ảnh
            ApiResponse<New> response = new ApiResponse<>(201, savedNew, "Tạo tin tức thành công");
            return ResponseEntity.status(201).body(response);
        } catch (IOException e) {
            ApiResponse<New> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Cập nhật tin tức
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<New>> updateNew(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        Optional<New> newsOptional = newService.getNewById(id);
        if (newsOptional.isPresent()) {
            New newObj = newsOptional.get();
            newObj.setTitle(title);
            newObj.setContent(content);
            newObj.setNewId(id); // Đặt ID cho tin tức

            try {
                New updatedNew = newService.saveNew(newObj, imageFile); // Gọi service với hình ảnh
                ApiResponse<New> response = new ApiResponse<>(200, updatedNew, "Cập nhật tin tức thành công");
                return ResponseEntity.ok(response);
            } catch (IOException e) {
                ApiResponse<New> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
                return ResponseEntity.status(500).body(response);
            }
        } else {
            ApiResponse<New> response = new ApiResponse<>(404, null, "Không tìm thấy tin tức");
            return ResponseEntity.status(404).body(response);
        }
    }

    // Xóa tin tức (Chuyển isDeleted thành true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNew(@PathVariable Long id) {
        Optional<New> newsOptional = newService.getNewById(id);
        if (newsOptional.isPresent()) {
            newService.deleteNew(id);
            ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa tin tức thành công");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy tin tức");
            return ResponseEntity.status(404).body(response);
        }
    }
}
