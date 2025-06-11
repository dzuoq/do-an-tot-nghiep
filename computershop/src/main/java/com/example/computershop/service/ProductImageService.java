package com.example.computershop.service;

import com.example.computershop.model.Product;
import com.example.computershop.model.ProductImage;
import com.example.computershop.repository.ProductImageRepository;
import com.example.computershop.repository.ProductRepository;
import com.example.computershop.utils.FirebaseImageUploadService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService;

    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProduct_ProductId(productId);
    }

    // Tạo hình ảnh sản phẩm mới
    public ProductImage createProductImage(Long productId, MultipartFile image) throws IOException {
        // Tìm product từ database trước khi liên kết với ProductImage
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Upload ảnh lên dịch vụ lưu trữ (Firebase trong ví dụ này)
        String imageUrl = firebaseImageUploadService.uploadImage(image);

        // Tạo đối tượng ProductImage và liên kết với product đã tìm thấy
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product); // Liên kết với sản phẩm đã tồn tại trong database
        productImage.setImageUrl(imageUrl);
        productImage.setIsDefault(false); // Đặt mặc định là false

        // Lưu ProductImage
        return productImageRepository.save(productImage);
    }

    // Cập nhật hình ảnh sản phẩm
    public ProductImage updateProductImage(Long imageId, MultipartFile image) throws IOException {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Hình ảnh không tồn tại"));

        String imageUrl = firebaseImageUploadService.uploadImage(image); // Upload lại ảnh mới
        productImage.setImageUrl(imageUrl); // Cập nhật URL
        return productImageRepository.save(productImage);
    }

    @Transactional
    public void deleteImageById(Long imageId) {
        // Kiểm tra xem hình ảnh có tồn tại hay không, nếu không thì ném ra ngoại lệ
        if (!productImageRepository.existsById(imageId)) {
            throw new IllegalArgumentException("Hình ảnh không tồn tại với ID: " + imageId);
        }

        // Xóa vĩnh viễn hình ảnh khỏi cơ sở dữ liệu
        productImageRepository.deleteById(imageId);
    }

    // Change default image for a product

    @Transactional
    public void changeDefaultImage(Long imageId, boolean isDefault) {
        // Tìm hình ảnh theo ID, nếu không có thì ném ngoại lệ
        ProductImage selectedImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));

        // Nếu trạng thái hiện tại của hình ảnh đã là isDefault = true và yêu cầu đặt
        // lại cũng là true, không làm gì cả
        if (selectedImage.getIsDefault() && isDefault) {
            return; // Không thực hiện thay đổi nếu hình ảnh đã là mặc định
        }

        // Đảm bảo rằng tất cả các hình ảnh của sản phẩm khác đều có isDefault = false
        productImageRepository.resetDefaultForProduct(selectedImage.getProduct().getProductId());

        // Đặt lại trạng thái mặc định cho hình ảnh được chọn
        selectedImage.setIsDefault(isDefault);
        productImageRepository.save(selectedImage);
    }

}
