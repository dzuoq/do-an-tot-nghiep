package com.example.computershop.service;

import com.example.computershop.model.Product;
import com.example.computershop.model.ProductImage;
import com.example.computershop.model.ProductVariation;
import com.example.computershop.repository.ProductRepository;
import com.example.computershop.repository.OrderDetailRepository;
import com.example.computershop.repository.ProductImageRepository;
import com.example.computershop.repository.ProductVariationRepository;
import com.example.computershop.repository.ReviewRepository;
import com.example.computershop.utils.FirebaseImageUploadService;
import com.example.computershop.utils.PaginationResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService; // Thêm service upload ảnh

    public PaginationResponse<Product> getAllProducts(String search, Long categoryId, Long brandId, int page, int limit,
            String sortField, String sortDirection) {
        // Xác định hướng sắp xếp (mặc định là asc)
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        // Tạo đối tượng Sort dựa trên sortField và direction
        Sort sort = Sort.by(direction, sortField);

        // Tạo đối tượng Pageable với sắp xếp
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        // Khởi tạo Specification
        Specification<Product> specification = Specification.where(null);

        // Thêm điều kiện tìm kiếm theo tên sản phẩm
        if (search != null && !search.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .like(root.get("productName"), "%" + search + "%"));
        }

        // Thêm điều kiện lọc theo categoryId
        if (categoryId != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("category").get("categoryId"), categoryId));
        }

        // Thêm điều kiện lọc theo brandId
        if (brandId != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brand").get("brandId"), brandId));
        }

        // Lọc các sản phẩm không bị xóa
        specification = specification
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDelete"), false));

        // Tìm kiếm sản phẩm với Specification và sắp xếp
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        // Tạo đối tượng phản hồi phân trang
        PaginationResponse<Product> response = new PaginationResponse<>();
        response.setContent(productPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());

        return response;
    }

    public List<Product> suggestProducts(Long userId, int limit) {
        List<Product> suggestedProducts = new ArrayList<>();

        if (userId != null) {
            List<Product> purchasedProducts = orderDetailRepository.findPurchasedProductsByUser(userId);

            if (!purchasedProducts.isEmpty()) {
                Set<Long> categoryIds = purchasedProducts.stream()
                        .map(product -> product.getCategory().getCategoryId())
                        .collect(Collectors.toSet());

                Set<Long> brandIds = purchasedProducts.stream()
                        .map(product -> product.getBrand().getBrandId())
                        .collect(Collectors.toSet());

                List<Product> relatedProducts = productRepository.findRelatedProducts(categoryIds, brandIds,
                        Pageable.unpaged());
                relatedProducts.removeAll(purchasedProducts);

                Map<Product, Double> productSimilarityScores = new HashMap<>();
                for (Product relatedProduct : relatedProducts) {
                    double similarityScore = calculateContentSimilarity(purchasedProducts, relatedProduct);
                    productSimilarityScores.put(relatedProduct, similarityScore);
                }

                suggestedProducts = productSimilarityScores.entrySet().stream()
                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                        .limit(limit)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            }
        }

        if (suggestedProducts.isEmpty()) {
            suggestedProducts = productRepository.findRandomProducts(PageRequest.of(0, limit));
        }

        return suggestedProducts;
    }

    private double calculateContentSimilarity(List<Product> purchasedProducts, Product targetProduct) {
        double score = 0.0;

        for (Product purchasedProduct : purchasedProducts) {
            if (purchasedProduct.getCategory().equals(targetProduct.getCategory())) {
                score += 0.5; // Điểm cao hơn nếu cùng category
            }
            if (purchasedProduct.getBrand().equals(targetProduct.getBrand())) {
                score += 0.3; // Điểm trung bình nếu cùng brand (publisher)
            }
            if (calculateDescriptionSimilarity(purchasedProduct.getDescription(),
                    targetProduct.getDescription()) > 0.3) {
                score += 0.2; // Điểm thấp hơn nếu mô tả sản phẩm có độ tương đồng cao
            }
        }

        return score;
    }

    private double calculateDescriptionSimilarity(String desc1, String desc2) {
        if (desc1 == null || desc2 == null)
            return 0.0;

        Set<String> words1 = new HashSet<>(Arrays.asList(desc1.toLowerCase().split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(desc2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return (double) intersection.size() / union.size();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public PaginationResponse<Product> getFilteredProducts(Boolean isNewProduct, Boolean isSale, Boolean isSpecial,
            int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        // Khởi tạo Specification
        Specification<Product> specification = Specification.where(null);

        // Thêm điều kiện lọc cho isNewProduct
        if (isNewProduct != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isNewProduct"), isNewProduct));
        }

        // Thêm điều kiện lọc cho isSale
        if (isSale != null) {
            specification = specification
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isSale"), isSale));
        }

        // Thêm điều kiện lọc cho isSpecial
        if (isSpecial != null) {
            specification = specification
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isSpecial"), isSpecial));
        }

        // Thêm điều kiện lọc cho isDelete phải là false
        specification = specification
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDelete"), false));

        // Thực hiện truy vấn để lấy sản phẩm theo điều kiện
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        // Tạo đối tượng phản hồi
        PaginationResponse<Product> response = new PaginationResponse<>();
        response.setContent(productPage.getContent()); // Danh sách sản phẩm
        response.setPage(page); // Trang hiện tại
        response.setLimit(limit); // Giới hạn số lượng sản phẩm trên mỗi trang
        response.setTotalElements(productPage.getTotalElements()); // Tổng số sản phẩm
        response.setTotalPages(productPage.getTotalPages()); // Tổng số trang

        return response; // Trả về phản hồi
    }

    public Double calculateAvgRating(Long productId) {
        // Logic tính avgRating từ đánh giá sản phẩm
        return reviewRepository.calculateAvgRatingForProduct(productId);
    }

    public Integer countReviews(Long productId) {
        return reviewRepository.countByProduct_ProductId(productId);
    }

    // Tạo sản phẩm mới
    public Product createProduct(Product product, MultipartFile[] images, List<ProductVariation> variations)
            throws IOException {
        // Lưu sản phẩm vào cơ sở dữ liệu
        Product savedProduct = productRepository.save(product);

        // Lưu hình ảnh nếu có
        if (images != null) {
            boolean isFirstImage = true; // Biến để theo dõi hình ảnh đầu tiên

            for (MultipartFile image : images) {
                String imageUrl = firebaseImageUploadService.uploadImage(image); // Lưu ảnh vào Firebase
                ProductImage productImage = new ProductImage(savedProduct, imageUrl);

                // Nếu là hình ảnh đầu tiên, đặt isDefault là true
                if (isFirstImage) {
                    productImage.setIsDefault(true);
                    isFirstImage = false; // Chuyển biến thành false sau khi đã đặt isDefault
                } else {
                    productImage.setIsDefault(false); // Các hình ảnh tiếp theo không phải là hình ảnh mặc định
                }

                productImageRepository.save(productImage);
            }
        }

        // Lưu biến thể nếu có
        if (variations != null) {
            for (ProductVariation variation : variations) {
                variation.setProduct(savedProduct); // Liên kết biến thể với sản phẩm
            }
            productVariationRepository.saveAll(variations);
        }

        return savedProduct; // Trả về sản phẩm đã lưu
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Long id, Product productDetails, MultipartFile[] images,
            List<ProductVariation> variations) throws IOException {
        // Kiểm tra sản phẩm có tồn tại
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return null; // Hoặc throw exception
        }

        Product product = optionalProduct.get();
        product.setProductName(productDetails.getProductName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setDiscount(productDetails.getDiscount());
        product.setBadge(productDetails.getBadge());
        product.setStock(productDetails.getStock());
        product.setIsNewProduct(productDetails.getIsNewProduct());
        product.setIsSale(productDetails.getIsSale());
        product.setIsSpecial(productDetails.getIsSpecial());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());

        // Cập nhật hình ảnh nếu có
        if (images != null) {
            // Xóa các hình ảnh cũ nếu cần
            productImageRepository.deleteAll(product.getImages());

            // Biến để theo dõi xem đã thêm hình ảnh nào hay chưa
            boolean isFirstImage = true;

            for (MultipartFile image : images) {
                String imageUrl = firebaseImageUploadService.uploadImage(image); // Lưu ảnh vào Firebase
                ProductImage productImage = new ProductImage(product, imageUrl);

                // Nếu là hình ảnh đầu tiên, đặt isDefault là true
                if (isFirstImage) {
                    productImage.setIsDefault(true);
                    isFirstImage = false; // Chỉ đặt isDefault cho hình ảnh đầu tiên
                } else {
                    productImage.setIsDefault(false); // Các hình ảnh tiếp theo không phải là hình ảnh mặc định
                }

                productImageRepository.save(productImage);
            }
        }

        // Cập nhật biến thể nếu có
        // Cập nhật biến thể nếu có
        if (variations != null) {
            // Lấy danh sách biến thể hiện tại của sản phẩm
            List<ProductVariation> existingVariations = productVariationRepository
                    .findByProduct_ProductId(product.getProductId());

            // Tìm các biến thể cần xóa (biến thể có trong existingVariations nhưng không có
            // trong variations mới)
            List<ProductVariation> variationsToRemove = existingVariations.stream()
                    .filter(existingVariation -> variations.stream().noneMatch(
                            newVariation -> newVariation.getVariationId().equals(existingVariation.getVariationId())))
                    .collect(Collectors.toList());

            // Xóa các biến thể không còn tồn tại
            if (!variationsToRemove.isEmpty()) {
                productVariationRepository.deleteAll(variationsToRemove);
            }

            // Cập nhật hoặc thêm mới các biến thể
            for (ProductVariation variation : variations) {
                variation.setProduct(product); // Liên kết biến thể với sản phẩm
            }
            productVariationRepository.saveAll(variations);
        }

        product = productRepository.save(product);
        return product; // Trả về sản phẩm đã lưu
    }

    // Xóa sản phẩm (đánh dấu là đã xóa)
    public void deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        productOptional.ifPresent(product -> {
            product.setIsDelete(true); // Đánh dấu sản phẩm là đã xóa
            productRepository.save(product); // Lưu lại sự thay đổi
        });
    }

}
