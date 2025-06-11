package com.example.computershop.service;

import com.example.computershop.model.New;
import com.example.computershop.repository.NewRepository;
import com.example.computershop.utils.FirebaseImageUploadService; // Thêm import cho dịch vụ tải ảnh
import com.example.computershop.utils.PaginationResponse;
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
public class NewService {

    @Autowired
    private NewRepository newRepository;

    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService; // Dịch vụ tải ảnh

    // Lấy tất cả các tin tức chưa bị xóa với phân trang, tìm kiếm và sắp xếp
    public PaginationResponse<New> getAllNews(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("publishDate").descending());
        Page<New> newsPage;

        // Tìm kiếm theo từ khóa nếu có
        if (keyword != null && !keyword.isEmpty()) {
            newsPage = newRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
        } else {
            newsPage = newRepository.findByIsDeletedFalse(pageable);
        }

        // Tạo đối tượng phản hồi tùy chỉnh
        PaginationResponse<New> response = new PaginationResponse<>();
        response.setContent(newsPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(newsPage.getTotalElements());
        response.setTotalPages(newsPage.getTotalPages());

        return response;
    }

    // Lấy tin tức theo ID
    public Optional<New> getNewById(Long newId) {
        return newRepository.findById(newId).filter(newObj -> !newObj.getIsDeleted());
    }

    // Lưu tin tức
    public New saveNew(New newObj, MultipartFile imageFile) throws IOException {
        // Nếu có tệp ảnh được cung cấp
        if (imageFile != null && !imageFile.isEmpty()) {
            // Tải ảnh lên Firebase và lưu URL vào newObj
            String imageUrl = firebaseImageUploadService.uploadImage(imageFile);
            newObj.setImage(imageUrl);
        }

        // Lưu tin tức vào cơ sở dữ liệu
        return newRepository.save(newObj);
    }

    // Xóa tin tức bằng cách cập nhật isDeleted thành true
    public void deleteNew(Long newId) {
        Optional<New> newsOptional = newRepository.findById(newId);
        newsOptional.ifPresent(news -> {
            news.setIsDeleted(true);
            newRepository.save(news);
        });
    }
}
