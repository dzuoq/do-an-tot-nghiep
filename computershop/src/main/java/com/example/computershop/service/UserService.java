package com.example.computershop.service;

import com.example.computershop.model.User;
import com.example.computershop.repository.UserRepository;
import com.example.computershop.utils.FirebaseImageUploadService;
import com.example.computershop.utils.PaginationResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseImageUploadService firebaseImageUploadService;
    @Autowired
    private JavaMailSender mailSender;

    // Lấy tất cả người dùng với phân trang và tìm kiếm
    public PaginationResponse<User> getAllUsers(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("fullName").ascending());
        Page<User> userPage;

        // Tìm kiếm theo từ khóa nếu có, chỉ lấy người dùng không bị xóa
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByIsDeleteFalseAndFullNameContainingOrEmailContainingOrPhoneNumberContaining(
                    true, keyword, keyword, keyword, pageable);
        } else {
            userPage = userRepository.findByIsDeleteFalse(pageable);
        }

        // Tạo đối tượng phản hồi tùy chỉnh
        PaginationResponse<User> response = new PaginationResponse<>();
        response.setContent(userPage.getContent());
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());

        return response;
    }

    // Lấy người dùng theo ID
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Tạo người dùng mới
    public String createUser(User user, MultipartFile imageFile) throws IOException {
        // Kiểm tra tính duy nhất của username, email và phoneNumber
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username đã tồn tại";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email đã tồn tại";
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return "Số điện thoại đã tồn tại";
        }

        // Xử lý upload avatar nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = firebaseImageUploadService.uploadImage(imageFile); // Tải avatar lên Firebase
            user.setAvatar(imageUrl); // Lưu URL ảnh vào User
        }

        userRepository.save(user); // Lưu User vào cơ sở dữ liệu
        return "Tạo người dùng thành công"; // Trả về thông điệp thành công
    }

    // Cập nhật người dùng
    public String updateUser(Long id, User userDetails, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Kiểm tra tính duy nhất của username, email và phoneNumber
        if (!user.getUsername().equals(userDetails.getUsername())
                && userRepository.existsByUsername(userDetails.getUsername())) {
            return "Username đã tồn tại";
        }
        if (!user.getEmail().equals(userDetails.getEmail()) && userRepository.existsByEmail(userDetails.getEmail())) {
            return "Email đã tồn tại";
        }
        if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber())
                && userRepository.existsByPhoneNumber(userDetails.getPhoneNumber())) {
            return "Số điện thoại đã tồn tại";
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setFullName(userDetails.getFullName());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = firebaseImageUploadService.uploadImage(imageFile); // Upload avatar mới
            user.setAvatar(imageUrl);
        }

        userRepository.save(user); // Lưu người dùng đã cập nhật
        return "Cập nhật người dùng thành công"; // Trả về thông điệp thành công
    }

    // Xóa người dùng bằng cách cập nhật isDelete thành true (soft delete)
    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setIsDelete(true); // Soft delete bằng cách đặt cờ isDelete thành true
            userRepository.save(existingUser); // Lưu người dùng đã xóa
            return true; // Trả về thông điệp thành công
        }
        return false; // Trả về thông điệp lỗi
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user); // Sử dụng repository để lưu người dùng vào cơ sở dữ liệu
    }

    public String sendResetPasswordToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return "Email không tồn tại trong hệ thống";
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString(); // Tạo mã token ngẫu nhiên
        user.setResetPasswordToken(token);
        userRepository.save(user); // Lưu token vào cơ sở dữ liệu

        // Gửi email với mã token
        String resetPasswordLink = "http://localhost:3000/reset-password?token=" + token;
        sendEmail(user.getEmail(), resetPasswordLink);

        return "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn";
    }

    private void sendEmail(String recipientEmail, String link) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(recipientEmail);
            helper.setSubject("Đặt lại mật khẩu");
            helper.setText("<p>Xin chào,</p>" +
                    "<p>Bạn đã yêu cầu đặt lại mật khẩu.</p>" +
                    "<p>Nhấn vào liên kết dưới đây để thay đổi mật khẩu:</p>" +
                    "<p><a href=\"" + link + "\">Đặt lại mật khẩu</a></p>", true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Gửi email thất bại", e);
        }

    }

    public String resetPassword(String token, String encodedPassword) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);

        if (!userOptional.isPresent()) {
            return "Token không hợp lệ hoặc đã hết hạn";
        }

        User user = userOptional.get();
        user.setPassword(encodedPassword); // Mật khẩu đã được mã hóa
        user.setResetPasswordToken(null); // Xóa token sau khi mật khẩu được đặt lại
        userRepository.save(user); // Lưu người dùng với mật khẩu mới

        return "Mật khẩu của bạn đã được cập nhật thành công";
    }

}
