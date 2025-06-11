package com.example.computershop.controller;

import com.example.computershop.dto.BrandDTO;
import com.example.computershop.dto.CategoryDTO;
import com.example.computershop.dto.ProductDTO;
import com.example.computershop.dto.ProductImageDTO;
import com.example.computershop.dto.ProductVariationDTO;
import com.example.computershop.model.Brand;
import com.example.computershop.model.Category;
import com.example.computershop.model.Product;
import com.example.computershop.model.ProductVariation;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.computershop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<ProductDTO>>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "productName") String sortField, // Trường dùng để sắp xếp
            @RequestParam(defaultValue = "asc") String sortDirection // Hướng sắp xếp: asc hoặc desc
    ) {

        PaginationResponse<Product> products = productService.getAllProducts(search, categoryId, brandId, page, limit,
                sortField, sortDirection);

        // Chuyển đổi sang DTO
        List<ProductDTO> productDTOs = products.getContent().stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setDescription(product.getDescription());
            dto.setDiscount(product.getDiscount());
            dto.setBadge(product.getBadge());
            dto.setStock(product.getStock());
            dto.setIsNewProduct(product.getIsNewProduct());
            dto.setIsSale(product.getIsSale());
            dto.setIsSpecial(product.getIsSpecial());

            // Chỉ lấy thông tin cần thiết từ category
            Category category = product.getCategory();
            if (category != null) {
                dto.setCategory(
                        new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
            }

            // Chỉ lấy thông tin cần thiết từ brand
            Brand brand = product.getBrand();
            if (brand != null) {
                dto.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
            }

            // Chuyển đổi danh sách biến thể và hình ảnh
            List<ProductVariationDTO> variationDTOs = product.getVariations().stream().map(variation -> {
                ProductVariationDTO variationDTO = new ProductVariationDTO();
                variationDTO.setVariationId(variation.getVariationId());
                variationDTO.setAttributeName(variation.getAttributeName());
                variationDTO.setAttributeValue(variation.getAttributeValue());
                variationDTO.setPrice(variation.getPrice());
                variationDTO.setQuantity(variation.getQuantity());
                return variationDTO;
            }).toList();

            List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
                ProductImageDTO imageDTO = new ProductImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImageUrl(image.getImageUrl());
                imageDTO.setIsDefault(image.getIsDefault()); // Đảm bảo rằng bạn đang set giá trị isDefault
                return imageDTO;
            }).toList();
            dto.setAvgRating(productService.calculateAvgRating(product.getProductId()));
            dto.setReviewCount(productService.countReviews(product.getProductId()));
            dto.setVariations(variationDTOs); // Danh sách biến thể
            dto.setImages(imageDTOs); // Danh sách hình ảnh
            dto.setIsDelete(product.getIsDelete());

            return dto;
        }).toList();

        PaginationResponse<ProductDTO> response = new PaginationResponse<>();
        response.setContent(productDTOs);
        response.setPage(products.getPage());
        response.setLimit(products.getLimit());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        ApiResponse<PaginationResponse<ProductDTO>> apiResponse = new ApiResponse<>(200, response,
                "Lấy danh sách sản phẩm thành công");
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> suggestProducts(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "10") int limit) {

        List<Product> suggestedProducts = productService.suggestProducts(userId, limit);

        if (suggestedProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, null, "Không tìm thấy sản phẩm gợi ý."));
        }

        // Chuyển đổi danh sách Product thành ProductDTO
        List<ProductDTO> productDTOs = suggestedProducts.stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setDescription(product.getDescription());
            dto.setDiscount(product.getDiscount());
            dto.setBadge(product.getBadge());
            dto.setStock(product.getStock());
            dto.setIsNewProduct(product.getIsNewProduct());
            dto.setIsSale(product.getIsSale());
            dto.setIsSpecial(product.getIsSpecial());

            // Chỉ lấy thông tin cần thiết từ category
            Category category = product.getCategory();
            if (category != null) {
                dto.setCategory(
                        new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
            }

            // Chỉ lấy thông tin cần thiết từ brand
            Brand brand = product.getBrand();
            if (brand != null) {
                dto.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
            }

            // Chuyển đổi danh sách biến thể và hình ảnh
            List<ProductVariationDTO> variationDTOs = product.getVariations().stream().map(variation -> {
                ProductVariationDTO variationDTO = new ProductVariationDTO();
                variationDTO.setVariationId(variation.getVariationId());
                variationDTO.setAttributeName(variation.getAttributeName());
                variationDTO.setAttributeValue(variation.getAttributeValue());
                variationDTO.setPrice(variation.getPrice());
                variationDTO.setQuantity(variation.getQuantity());
                return variationDTO;
            }).toList();

            List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
                ProductImageDTO imageDTO = new ProductImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImageUrl(image.getImageUrl());
                imageDTO.setIsDefault(image.getIsDefault()); // Đảm bảo rằng bạn đang set giá trị isDefault
                return imageDTO;
            }).toList();

            dto.setVariations(variationDTOs);
            dto.setImages(imageDTOs);
            dto.setAvgRating(productService.calculateAvgRating(product.getProductId()));
            dto.setReviewCount(productService.countReviews(product.getProductId()));
            dto.setIsDelete(product.getIsDelete());

            return dto;
        }).toList();

        ApiResponse<List<ProductDTO>> apiResponse = new ApiResponse<>(200, productDTOs, "Gợi ý sản phẩm thành công");
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/filtered")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductDTO>>> getFilteredProducts(
            @RequestParam(required = false) Boolean isNewProduct,
            @RequestParam(required = false) Boolean isSale,
            @RequestParam(required = false) Boolean isSpecial,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        PaginationResponse<Product> products = productService.getFilteredProducts(isNewProduct, isSale, isSpecial, page,
                limit);

        // Chuyển đổi sang DTO
        List<ProductDTO> productDTOs = products.getContent().stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setDescription(product.getDescription());
            dto.setDiscount(product.getDiscount());
            dto.setBadge(product.getBadge());
            dto.setStock(product.getStock());
            dto.setIsNewProduct(product.getIsNewProduct());
            dto.setIsSale(product.getIsSale());
            dto.setIsSpecial(product.getIsSpecial());

            // Chỉ lấy thông tin cần thiết từ category
            Category category = product.getCategory();
            if (category != null) {
                dto.setCategory(
                        new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
            }

            // Chỉ lấy thông tin cần thiết từ brand
            Brand brand = product.getBrand();
            if (brand != null) {
                dto.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
            }

            // Chuyển đổi danh sách biến thể và hình ảnh
            List<ProductVariationDTO> variationDTOs = product.getVariations().stream().map(variation -> {
                ProductVariationDTO variationDTO = new ProductVariationDTO();
                variationDTO.setVariationId(variation.getVariationId());
                variationDTO.setAttributeName(variation.getAttributeName());
                variationDTO.setAttributeValue(variation.getAttributeValue());
                variationDTO.setPrice(variation.getPrice());
                variationDTO.setQuantity(variation.getQuantity());
                return variationDTO;
            }).toList();

            List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
                ProductImageDTO imageDTO = new ProductImageDTO();
                imageDTO.setImageId(image.getImageId());
                imageDTO.setImageUrl(image.getImageUrl());
                imageDTO.setIsDefault(image.getIsDefault()); // Đảm bảo rằng bạn đang set giá trị isDefault
                return imageDTO;
            }).toList();
            dto.setAvgRating(productService.calculateAvgRating(product.getProductId()));
            dto.setReviewCount(productService.countReviews(product.getProductId()));
            dto.setVariations(variationDTOs); // Danh sách biến thể
            dto.setImages(imageDTOs); // Danh sách hình ảnh
            dto.setIsDelete(product.getIsDelete());

            return dto;
        }).toList();

        PaginationResponse<ProductDTO> response = new PaginationResponse<>();
        response.setContent(productDTOs);
        response.setPage(products.getPage());
        response.setLimit(products.getLimit());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());

        ApiResponse<PaginationResponse<ProductDTO>> apiResponse = new ApiResponse<>(200, response,
                "Lấy danh sách sản phẩm thành công");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        // Gọi service để lấy sản phẩm theo ID
        Product product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, null, "Sản phẩm không tồn tại"));
        }

        // Chuyển đổi Product thành ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setProductName(product.getProductName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setDiscount(product.getDiscount());
        productDTO.setBadge(product.getBadge());
        productDTO.setStock(product.getStock());
        productDTO.setIsNewProduct(product.getIsNewProduct());
        productDTO.setIsSale(product.getIsSale());
        productDTO.setIsSpecial(product.getIsSpecial());

        // Chỉ lấy thông tin cần thiết từ category
        Category category = product.getCategory();
        if (category != null) {
            productDTO.setCategory(
                    new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
        }

        // Chỉ lấy thông tin cần thiết từ brand
        Brand brand = product.getBrand();
        if (brand != null) {
            productDTO.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
        }

        // Chuyển đổi danh sách biến thể và hình ảnh
        List<ProductVariationDTO> variationDTOs = product.getVariations().stream().map(variation -> {
            ProductVariationDTO variationDTO = new ProductVariationDTO();
            variationDTO.setVariationId(variation.getVariationId());
            variationDTO.setAttributeName(variation.getAttributeName());
            variationDTO.setAttributeValue(variation.getAttributeValue());
            variationDTO.setPrice(variation.getPrice());
            variationDTO.setQuantity(variation.getQuantity());
            return variationDTO;
        }).toList();

        List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
            ProductImageDTO imageDTO = new ProductImageDTO();
            imageDTO.setImageId(image.getImageId());
            imageDTO.setImageUrl(image.getImageUrl());
            imageDTO.setIsDefault(image.getIsDefault()); // Đảm bảo rằng bạn đang set giá trị isDefault
            return imageDTO;
        }).toList();
        productDTO.setAvgRating(productService.calculateAvgRating(product.getProductId()));
        productDTO.setReviewCount(productService.countReviews(product.getProductId()));
        productDTO.setVariations(variationDTOs); // Danh sách biến thể
        productDTO.setImages(imageDTOs); // Danh sách hình ảnh
        productDTO.setIsDelete(product.getIsDelete());

        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>(200, productDTO, "Lấy sản phẩm thành công");
        return ResponseEntity.ok(apiResponse);
    }

    // Tạo sản phẩm mới
    @Autowired
    private ObjectMapper objectMapper; // Thêm ObjectMapper để chuyển đổi JSON

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @RequestParam("productName") String productName,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "discount", required = false) BigDecimal discount,
            @RequestParam(value = "badge", required = false) String badge,
            @RequestParam("stock") int stock,
            @RequestParam(value = "isNewProduct", required = false) Boolean isNewProduct,
            @RequestParam(value = "isSale", required = false) Boolean isSale,
            @RequestParam(value = "isSpecial", required = false) Boolean isSpecial,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("brandId") Long brandId,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "variations", required = false) String variations)
            throws IOException {

        Product product = new Product(productName, price, description, discount, badge, stock, isNewProduct, isSale,
                isSpecial, null, null);
        product.setCategory(new Category(categoryId));
        product.setBrand(new Brand(brandId));

        // Chuyển đổi chuỗi JSON thành danh sách các biến thể
        List<ProductVariation> variationList = null; // Khởi tạo biến thể list là null
        if (variations != null && !variations.isEmpty()) { // Kiểm tra nếu variations không null
            variationList = objectMapper.readValue(variations,
                    new TypeReference<List<ProductVariation>>() {
                    });
        }

        Product createdProduct = productService.createProduct(product, images, variationList);

        // Chuyển đổi Product thành ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(createdProduct.getProductId());
        productDTO.setProductName(createdProduct.getProductName());
        productDTO.setPrice(createdProduct.getPrice());
        productDTO.setDescription(createdProduct.getDescription());
        productDTO.setDiscount(createdProduct.getDiscount());
        productDTO.setBadge(createdProduct.getBadge());
        productDTO.setStock(createdProduct.getStock());
        productDTO.setIsNewProduct(createdProduct.getIsNewProduct());
        productDTO.setIsSale(createdProduct.getIsSale());
        productDTO.setIsSpecial(createdProduct.getIsSpecial());

        // Chỉ lấy thông tin cần thiết từ category
        Category category = createdProduct.getCategory();
        if (category != null) {
            productDTO.setCategory(
                    new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
        }

        // Chỉ lấy thông tin cần thiết từ brand
        Brand brand = createdProduct.getBrand();
        if (brand != null) {
            productDTO.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
        }

        // productDTO.setVariations(createdProduct.getVariations()); // Danh sách biến
        // thể
        // productDTO.setImages(createdProduct.getImages()); // Danh sách hình ảnh
        productDTO.setIsDelete(createdProduct.getIsDelete());

        ApiResponse<ProductDTO> response = new ApiResponse<>(201, productDTO, "Tạo sản phẩm thành công");
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "discount", required = false) BigDecimal discount,
            @RequestParam(value = "badge", required = false) String badge,
            @RequestParam("stock") int stock,
            @RequestParam(value = "isNewProduct", required = false) Boolean isNewProduct,
            @RequestParam(value = "isSale", required = false) Boolean isSale,
            @RequestParam(value = "isSpecial", required = false) Boolean isSpecial,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("brandId") Long brandId,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "variations", required = false) String variations) // Accept variations as JSON string
            throws IOException {

        Product productDetails = new Product(productName, price, description, discount, badge, stock, isNewProduct,
                isSale, isSpecial, null, null);
        productDetails.setCategory(new Category(categoryId));
        productDetails.setBrand(new Brand(brandId));

        // Convert JSON string to List<ProductVariation>
        List<ProductVariation> variationList = null; // Initialize as null
        if (variations != null && !variations.isEmpty()) { // Check if variations is not null or empty
            variationList = objectMapper.readValue(variations,
                    new TypeReference<List<ProductVariation>>() {
                    });
        }

        // Update the product and retrieve the updated product
        Product updatedProduct = productService.updateProduct(id, productDetails, images, variationList);

        // Convert updated product to ProductDTO for response
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(updatedProduct.getProductId());
        productDTO.setProductName(updatedProduct.getProductName());
        productDTO.setPrice(updatedProduct.getPrice());
        productDTO.setDescription(updatedProduct.getDescription());
        productDTO.setDiscount(updatedProduct.getDiscount());
        productDTO.setBadge(updatedProduct.getBadge());
        productDTO.setStock(updatedProduct.getStock());
        productDTO.setIsNewProduct(updatedProduct.getIsNewProduct());
        productDTO.setIsSale(updatedProduct.getIsSale());
        productDTO.setIsSpecial(updatedProduct.getIsSpecial());

        // Only retrieve necessary information from category
        Category category = updatedProduct.getCategory();
        if (category != null) {
            productDTO.setCategory(
                    new CategoryDTO(category.getCategoryId(), category.getCategoryName(), category.getImage()));
        }

        // Only retrieve necessary information from brand
        Brand brand = updatedProduct.getBrand();
        if (brand != null) {
            productDTO.setBrand(new BrandDTO(brand.getBrandId(), brand.getBrandName(), brand.getImage()));
        }

        // Map variations to ProductVariationDTO
        // List<ProductVariationDTO> productVariationDTOs =
        // updatedProduct.getVariations().stream().map(variation -> {
        // ProductVariationDTO variationDTO = new ProductVariationDTO();
        // variationDTO.setVariationId(variation.getVariationId());
        // variationDTO.setAttributeName(variation.getAttributeName());
        // variationDTO.setAttributeValue(variation.getAttributeValue());
        // variationDTO.setPrice(variation.getPrice());
        // variationDTO.setQuantity(variation.getQuantity());
        // return variationDTO;
        // }).toList();
        // productDTO.setVariations(productVariationDTOs); // List of variations
        // productDTO.setImages(updatedProduct.getImages()); // List of image URLs
        productDTO.setIsDelete(updatedProduct.getIsDelete());

        ApiResponse<ProductDTO> response = new ApiResponse<>(200, productDTO, "Cập nhật sản phẩm thành công");
        return ResponseEntity.ok(response);
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa sản phẩm thành công");
        return ResponseEntity.ok(response);
    }
}
