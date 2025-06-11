package com.example.computershop.controller;

import com.example.computershop.model.Brand;
import com.example.computershop.service.BrandService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Lấy tất cả các thương hiệu với phân trang, tìm kiếm và sắp xếp
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<Brand>>> getAllBrands(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PaginationResponse<Brand> brands = brandService.getAllBrands(keyword, page, limit);
        ApiResponse<PaginationResponse<Brand>> response = new ApiResponse<>(200, brands, "Lấy thương hiệu thành công");
        return ResponseEntity.ok(response);
    }

    // Lấy thương hiệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Brand>> getBrandById(@PathVariable Long id) {
        Optional<Brand> brand = brandService.getBrandById(id);
        return brand.map(b -> {
            ApiResponse<Brand> response = new ApiResponse<>(200, b, "Lấy thương hiệu thành công");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<Brand> response = new ApiResponse<>(404, null, "Không tìm thấy thương hiệu");
            return ResponseEntity.status(404).body(response);
        });
    }

    // Thêm thương hiệu mới và tải ảnh lên Firebase
    @PostMapping
    public ResponseEntity<ApiResponse<Brand>> createBrand(
            @RequestParam("brandName") String brandName,
            @RequestParam("description") String description,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            // Tạo đối tượng Brand mới
            Brand brand = new Brand();
            brand.setBrandName(brandName);
            brand.setDescription(description);
            brand.setIsDelete(false); // Mặc định isDelete là false

            // Lưu thương hiệu và ảnh
            Brand savedBrand = brandService.saveBrand(brand, imageFile);
            ApiResponse<Brand> response = new ApiResponse<>(201, savedBrand, "Tạo thương hiệu thành công");
            return ResponseEntity.status(201).body(response);
        } catch (IOException e) {
            ApiResponse<Brand> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Cập nhật thương hiệu
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(
            @PathVariable Long id,
            @RequestParam("brandName") String brandName,
            @RequestParam("description") String description,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Optional<Brand> brandOptional = brandService.getBrandById(id);
            if (brandOptional.isPresent()) {
                Brand brand = brandOptional.get();
                brand.setBrandName(brandName);
                brand.setDescription(description);

                // Lưu cập nhật thương hiệu và (nếu có) cập nhật ảnh
                Brand savedBrand = brandService.saveBrand(brand, imageFile);
                ApiResponse<Brand> response = new ApiResponse<>(200, savedBrand, "Cập nhật thương hiệu thành công");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Brand> response = new ApiResponse<>(404, null, "Không tìm thấy thương hiệu");
                return ResponseEntity.status(404).body(response);
            }
        } catch (IOException e) {
            ApiResponse<Brand> response = new ApiResponse<>(500, null, "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Xóa thương hiệu (Chuyển isDelete thành true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        Optional<Brand> brandOptional = brandService.getBrandById(id);
        if (brandOptional.isPresent()) {
            brandService.deleteBrand(id);
            ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa thương hiệu thành công");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy thương hiệu");
            return ResponseEntity.status(404).body(response);
        }
    }
}
