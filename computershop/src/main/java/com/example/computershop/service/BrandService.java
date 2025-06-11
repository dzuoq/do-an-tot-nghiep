package com.example.computershop.service;

import com.example.computershop.model.Brand;
import com.example.computershop.repository.BrandRepository;
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
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService;

    // Lấy tất cả các thương hiệu chưa bị xóa với phân trang, tìm kiếm và sắp xếp
    public PaginationResponse<Brand> getAllBrands(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Brand> brandPage;

        // Tìm kiếm theo từ khóa nếu có
        if (keyword != null && !keyword.isEmpty()) {
            brandPage = brandRepository.findByBrandNameContainingAndIsDeleteFalse(keyword, pageable);
        } else {
            brandPage = brandRepository.findByIsDeleteFalse(pageable);
        }

        // Tạo đối tượng phản hồi tùy chỉnh
        PaginationResponse<Brand> response = new PaginationResponse<>();
        response.setContent(brandPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(brandPage.getTotalElements());
        response.setTotalPages(brandPage.getTotalPages());

        return response;
    }

    // Lấy thương hiệu theo ID
    public Optional<Brand> getBrandById(Long brandId) {
        return brandRepository.findById(brandId).filter(brand -> !brand.getIsDelete());
    }

    // Thêm hoặc cập nhật thương hiệu với việc tải ảnh lên Firebase
    public Brand saveBrand(Brand brand, MultipartFile imageFile) throws IOException {
        // Nếu có tệp ảnh được cung cấp
        if (imageFile != null && !imageFile.isEmpty()) {
            // Tải ảnh lên Firebase và lưu URL vào brand
            String imageUrl = firebaseImageUploadService.uploadImage(imageFile);
            brand.setImage(imageUrl);
        }

        // Lưu thương hiệu vào cơ sở dữ liệu
        return brandRepository.save(brand);
    }

    // Xóa thương hiệu bằng cách cập nhật isDelete thành true
    public void deleteBrand(Long brandId) {
        Optional<Brand> brand = brandRepository.findById(brandId);
        brand.ifPresent(b -> {
            b.setIsDelete(true);
            brandRepository.save(b);
        });
    }
}
