package com.example.computershop.controller;

import com.example.computershop.model.User;
import com.example.computershop.security.CustomUserDetailsService;
import com.example.computershop.utils.ApiResponse;
import com.example.computershop.utils.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy tất cả người dùng với phân trang và tìm kiếm
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<User>>> getAllUsers(
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        PaginationResponse<User> users = userDetailsService.getAllUsers(searchText, page, limit);
        ApiResponse<PaginationResponse<User>> response = new ApiResponse<>(200, users,
                "Lấy danh sách người dùng thành công");
        return ResponseEntity.ok(response);
    }

    // Lấy người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userDetailsService.getUserById(id);
        return user.map(u -> {
            ApiResponse<User> response = new ApiResponse<>(200, u, "Lấy người dùng thành công");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            ApiResponse<User> response = new ApiResponse<>(404, null, "Không tìm thấy người dùng");
            return ResponseEntity.status(404).body(response);
        });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("fullName") String fullName,
            @RequestParam("role") User.Role role,
            @RequestParam("address") String address,
            @RequestPart(value = "avatar", required = false) MultipartFile imageFile) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setFullName(fullName);
            user.setRole(role);
            user.setAddress(address);
            user.setIsDelete(false); // Người dùng không bị xóa mặc định

            String message = userDetailsService.createUser(user, imageFile);
            ApiResponse<User> response = new ApiResponse<>(201, user, message);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(500, null, "Lỗi khi tạo người dùng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Cập nhật người dùng
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("fullName") String fullName,
            @RequestParam("role") User.Role role,
            @RequestParam("address") String address,
            @RequestPart(value = "avatar", required = false) MultipartFile imageFile) {
        try {
            User userDetails = new User();
            userDetails.setUsername(username);
            userDetails.setEmail(email);
            userDetails.setPhoneNumber(phoneNumber);
            userDetails.setFullName(fullName);
            userDetails.setRole(role);
            userDetails.setAddress(address);

            // Cập nhật người dùng và kiểm tra tính duy nhất
            String message = userDetailsService.updateUser(id, userDetails, imageFile);
            ApiResponse<User> response = new ApiResponse<>(200, userDetails, message);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Trả về thông điệp lỗi nếu không tìm thấy người dùng hoặc lỗi khác
            ApiResponse<User> response = new ApiResponse<>(400, null, e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(500, null, "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Xóa người dùng
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            boolean isDeleted = userDetailsService.deleteUser(id);
            if (isDeleted) {
                ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa người dùng thành công");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy người dùng để xóa");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(500, null, "Lỗi khi xóa người dùng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // API đổi mật khẩu
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            // Lấy thông tin người dùng theo ID
            Optional<User> userOptional = userDetailsService.getUserById(id);
            if (!userOptional.isPresent()) {
                ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy người dùng");
                return ResponseEntity.status(404).body(response);
            }

            User user = userOptional.get();

            // Kiểm tra mật khẩu hiện tại có đúng không
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                ApiResponse<Void> response = new ApiResponse<>(400, null, "Mật khẩu hiện tại không chính xác");
                return ResponseEntity.status(400).body(response);
            }

            // Mã hóa và cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));
            userDetailsService.saveUser(user); // Cập nhật thông tin người dùng

            ApiResponse<Void> response = new ApiResponse<>(200, null, "Đổi mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(500, null, "Lỗi khi đổi mật khẩu: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
