package com.example.computershop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.computershop.model.User;
import com.example.computershop.service.UserService;
import com.example.computershop.utils.PaginationResponse;

import io.jsonwebtoken.io.IOException;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lấy thông tin người dùng từ UserService
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Trả về đối tượng UserDetails là lớp User của bạn
        return user;
    }

    // Thêm phương thức để lấy tất cả người dùng
    public PaginationResponse<User> getAllUsers(String keyword, int page, int limit) {
        return userService.getAllUsers(keyword, page, limit);
    }

    // Thêm phương thức để lấy người dùng theo ID
    public Optional<User> getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    // Thêm phương thức để tạo người dùng
    public String createUser(User user, MultipartFile imageFile) throws IOException, java.io.IOException {
        return userService.createUser(user, imageFile);
    }

    // Thêm phương thức để cập nhật người dùng
    public String updateUser(Long id, User userDetails, MultipartFile imageFile)
            throws IOException, java.io.IOException {
        return userService.updateUser(id, userDetails, imageFile);
    }

    // Thêm phương thức để xóa người dùng
    public boolean deleteUser(Long userId) {
        return userService.deleteUser(userId);
    }

    public void saveUser(User user) {
        userService.saveUser(user); // Lưu người dùng bằng UserService
    }
}
