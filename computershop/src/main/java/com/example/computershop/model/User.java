package com.example.computershop.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String address;
    private Boolean isDelete = false;

    // Constructors
    public User() {
    }

    public User(String username, String password, String email, String phoneNumber, String fullName, String avatar,
            Role role, String address, Boolean isDelete) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.avatar = avatar;
        this.role = role;
        this.address = address;
        this.isDelete = isDelete;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    // Enum for Role
    public enum Role {
        ADMIN, STAFF, CUSTOMER
    }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Nếu bạn có các quyền khác nhau, bạn có thể trả về danh sách các quyền ở đây.
        // Nếu không, có thể trả về danh sách rỗng.
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Tùy chỉnh nếu bạn muốn thêm logic kiểm tra
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tùy chỉnh nếu bạn muốn thêm logic kiểm tra
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Tùy chỉnh nếu bạn muốn thêm logic kiểm tra
    }

    @Override
    public boolean isEnabled() {
        return !isDelete; // Nếu tài khoản bị đánh dấu là "xóa", bạn có thể vô hiệu hóa tài khoản
    }
}
