package com.example.computershop.service;

import com.example.computershop.dto.OrderDTO;
import com.example.computershop.dto.OrderDetailDTO;
import com.example.computershop.dto.ProductImageDTO;
import com.example.computershop.dto.UserDTO;
import com.example.computershop.dto.AddressBookDTO;
import com.example.computershop.model.Order;
import com.example.computershop.model.OrderDetail;
import com.example.computershop.model.Product;
import com.example.computershop.model.ProductVariation;
import com.example.computershop.repository.OrderDetailRepository;
import com.example.computershop.repository.OrderRepository;
import com.example.computershop.repository.ProductRepository;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(Order orderRequest) throws Exception {
        Order savedOrder = orderRepository.save(orderRequest);

        if (orderRequest.getOrderDetails() != null) {
            for (OrderDetail detail : orderRequest.getOrderDetails()) {
                detail.setOrder(savedOrder);

                // Tải sản phẩm từ database dựa trên productId để đảm bảo đầy đủ dữ liệu
                Product product = productRepository.findById(detail.getProduct().getProductId())
                        .orElseThrow(() -> new Exception(
                                "Không tìm thấy sản phẩm với ID: " + detail.getProduct().getProductId()));

                int currentStock = product.getStock();
                int quantity = detail.getQuantity();

                // Log currentStock và quantity
                System.out.println("Product ID: " + product.getProductId());
                System.out.println("Current stock: " + currentStock);
                System.out.println("Order detail quantity: " + quantity);

                // Kiểm tra nếu stock đủ đáp ứng
                if (currentStock >= quantity) {
                    // Trừ stock của sản phẩm
                    product.setStock(currentStock - quantity);

                    // Cập nhật sản phẩm vào cơ sở dữ liệu
                    productRepository.save(product);

                    // Gán product đã tải cho OrderDetail và lưu chi tiết đơn hàng
                    detail.setProduct(product);
                    orderDetailRepository.save(detail);
                } else {
                    throw new Exception("Sản phẩm " + product.getProductName() + " không đủ tồn kho.");
                }
            }
        }
        return savedOrder;
    }

    public Page<Order> getAllOrders(Pageable pageable, String code, String status, String method) {
        Specification<Order> spec = Specification.where(null);

        if (code != null && !code.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("code"), code));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
        }

        if (method != null && !method.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("method"), method));
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "date"));

        return orderRepository.findAll(spec, sortedPageable);
    }

    public Page<Order> getOrdersByUserId(Long userId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "date"));

        return orderRepository.findByUser_UserId(userId, sortedPageable);
    }

    public Order changeOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // Method to find order by orderCode
    public Optional<Order> findOrderByCode(String orderCode) {
        return orderRepository.findByCode(orderCode);
    }

    public void sendOrderConfirmationEmail(String orderCode) throws MessagingException {
        Optional<Order> optionalOrder = findOrderByCode(orderCode);

        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Order not found with code: " + orderCode);
        }

        Order order = optionalOrder.get();

        DecimalFormat df = new DecimalFormat("#,###");

        // Chuyển đổi discount từ BigDecimal sang giá trị số
        BigDecimal discount = order.getDiscount();
        BigDecimal discountPercentage = discount.divide(BigDecimal.valueOf(100));

        // Tính tạm tính và giảm giá
        BigDecimal subtotal = order.getTotalPrice().divide(BigDecimal.ONE.subtract(discountPercentage),
                BigDecimal.ROUND_HALF_UP);
        BigDecimal discountAmount = subtotal.multiply(discountPercentage);

        // Tạo nội dung HTML
        String htmlContent = "<div style='font-family: \"Roboto\", sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e2e2;'>"
                + "<h2 style='color: #ff9900; font-weight: bold;'>Xác nhận đơn hàng</h2>"
                + "<p>Xin chào " + order.getAddressBook().getRecipientName() + ",</p>"
                + "<p>Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi.</p>"

                + "<h3 style='font-weight: bold; border-bottom: 2px solid #e2e2e2; padding-bottom: 10px;'>Sản phẩm đã đặt</h3>"
                + order.getOrderDetails().stream().map(
                        item -> "<div style='display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #e2e2e2;'>"
                                + "<div><img src='" + item.getProduct().getDefaultImage().getImageUrl() + "' alt='"
                                + item.getProduct().getProductName()
                                + "' style='width: 50px; height: 50px; object-fit: cover;'></div>"
                                + "<div style='flex: 1; padding-left: 4px;'><p style='font-weight: bold;'>"
                                + item.getProduct().getProductName() + "</p>"
                                + "<p>Lựa chọn: " +
                                (item.getProductVariation() != null
                                        && item.getProductVariation().getAttributeValue() != null
                                                ? item.getProductVariation().getAttributeValue()
                                                : "không có")
                                +
                                "</p>"

                                + "<p>Số lượng: " + item.getQuantity() + "</p></div>"
                                + "<div style='text-align: right; margin-left: 10px'><p>" + df.format(item.getPrice())
                                + "₫</p></div></div>")
                        .collect(Collectors.joining())

                + "<div style='border-top: 2px solid #e2e2e2; padding: 20px 0;'>"
                + "<p><strong>Tạm tính:</strong> " + df.format(subtotal) + "₫</p>"
                + "<p><strong>Giảm giá:</strong> -" + df.format(discountAmount) + "₫</p>"
                + "<p><strong>Tổng cộng:</strong> <span style='color: #ff9900;'>" + df.format(order.getTotalPrice())
                + "₫</span></p></div>"

                + "<h3 style='font-weight: bold; margin-top: 20px;'>Thông tin giao hàng</h3>"
                + "<p>" + order.getAddressBook().getRecipientName() + "</p>"
                + "<p>" + order.getAddressBook().getAddress() + ", " + order.getAddressBook().getWard() + ", "
                + order.getAddressBook().getDistrict() + ", " + order.getAddressBook().getCity() + "</p>"
                + "<p>" + order.getAddressBook().getPhoneNumber() + "</p>"

                + "<h3 style='font-weight: bold; margin-top: 20px;'>Thanh toán</h3>"
                + "<p>" + order.getPaymentMethod() + " - " + order.getStatus() + "</p>"

                + "<p style='margin-top: 30px; font-size: 12px;'>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với <a href='#' style='color: #ff9900;'>Dịch vụ khách hàng</a>.</p>"
                + "</div>";

        // Gửi email xác nhận
        emailService.sendOrderConfirmationEmail(order.getAddressBook().getEmail(),
                "Xác nhận đơn hàng - " + order.getCode(), htmlContent);
    }

    // Method to convert Order to OrderDTO
    public OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCode(order.getCode());
        orderDTO.setDate(order.getDate());
        orderDTO.setNote(order.getNote());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setDiscount(order.getDiscount());
        orderDTO.setStatus(order.getStatus());

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(order.getUser().getUserId());
        userDTO.setUsername(order.getUser().getUsername());
        userDTO.setEmail(order.getUser().getEmail());
        userDTO.setPhoneNumber(order.getUser().getPhoneNumber());
        userDTO.setFullName(order.getUser().getFullName());
        orderDTO.setUser(userDTO);

        AddressBookDTO addressBookDTO = new AddressBookDTO();
        addressBookDTO.setAddressBookId(order.getAddressBook().getAddressBookId());
        addressBookDTO.setRecipientName(order.getAddressBook().getRecipientName());
        addressBookDTO.setPhoneNumber(order.getAddressBook().getPhoneNumber());
        addressBookDTO.setAddress(order.getAddressBook().getAddress());
        addressBookDTO.setWard(order.getAddressBook().getWard());
        addressBookDTO.setDistrict(order.getAddressBook().getDistrict());
        addressBookDTO.setCity(order.getAddressBook().getCity());
        addressBookDTO.setEmail(order.getAddressBook().getEmail());
        orderDTO.setAddressBook(addressBookDTO);

        List<OrderDetailDTO> orderDetailDTOList = order.getOrderDetails().stream().map(detail -> {
            OrderDetailDTO detailDTO = new OrderDetailDTO();
            detailDTO.setOrderDetailId(detail.getOrderDetailId());
            detailDTO.setProductName(detail.getProduct().getProductName());
            detailDTO.setProductQuantity(detail.getQuantity());
            detailDTO.setPrice(detail.getPrice());

            ProductImageDTO productImageDTO = new ProductImageDTO();
            if (detail.getProduct().getDefaultImage() != null) {
                productImageDTO.setImageUrl(detail.getProduct().getDefaultImage().getImageUrl());
                detailDTO.setProductImages(productImageDTO);
            }

            ProductVariation productVariation = detail.getProductVariation();
            if (productVariation != null) {
                detailDTO.setVariationName(productVariation.getAttributeValue());
            } else {
                detailDTO.setVariationName(null);
            }

            return detailDTO;
        }).collect(Collectors.toList());

        orderDTO.setOrderDetails(orderDetailDTOList);

        return orderDTO;
    }
}
