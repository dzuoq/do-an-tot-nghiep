package com.example.computershop.controller;

import com.example.computershop.dto.ProductImageDTO;
import com.example.computershop.model.ProductImage;
import com.example.computershop.service.ProductImageService;
import com.example.computershop.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    // Lấy tất cả hình ảnh của một sản phẩm
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductImageDTO>>> getProductImages(@PathVariable Long productId) {
        List<ProductImage> images = productImageService.getImagesByProductId(productId);

        // Chuyển đổi danh sách ProductImage sang ProductImageDTO
        List<ProductImageDTO> imageDTOs = images.stream()
                .map(image -> new ProductImageDTO(image.getImageId(), image.getImageUrl(), image.getIsDefault())) // Thêm
                                                                                                                  // isDefault
                .collect(Collectors.toList());

        // Trả về danh sách hình ảnh DTO
        ApiResponse<List<ProductImageDTO>> response = new ApiResponse<>(200, imageDTOs,
                "Lấy hình ảnh sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    // Tạo hình ảnh sản phẩm mới
    @PostMapping
    public ResponseEntity<ApiResponse<ProductImageDTO>> createProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {

        ProductImage createdImage = productImageService.createProductImage(productId, image);
        ProductImageDTO imageDTO = new ProductImageDTO(createdImage.getImageId(), createdImage.getImageUrl(),
                createdImage.getIsDefault());

        ApiResponse<ProductImageDTO> response = new ApiResponse<>(201, imageDTO, "Tạo hình ảnh sản phẩm thành công");
        return ResponseEntity.status(201).body(response);
    }

    // Cập nhật hình ảnh sản phẩm
    @PutMapping("/{imageId}")
    public ResponseEntity<ApiResponse<ProductImageDTO>> updateProductImage(
            @PathVariable Long imageId,
            @RequestParam("image") MultipartFile image) throws IOException {

        ProductImage updatedImage = productImageService.updateProductImage(imageId, image);
        ProductImageDTO imageDTO = new ProductImageDTO(updatedImage.getImageId(), updatedImage.getImageUrl(),
                updatedImage.getIsDefault()); // Thêm isDefault

        ApiResponse<ProductImageDTO> response = new ApiResponse<>(200, imageDTO,
                "Cập nhật hình ảnh sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @PathVariable Long imageId) {
        productImageService.deleteImageById(imageId);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa hình ảnh sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    // Change the default image for the product
    @PutMapping("/{imageId}/default")
    public ResponseEntity<ApiResponse<Void>> changeDefaultImage(
            @PathVariable Long imageId,
            @RequestParam boolean isDefault) {
        productImageService.changeDefaultImage(imageId, isDefault);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Cập nhật trạng thái mặc định hình ảnh thành công");
        return ResponseEntity.ok(response);
    }

}
