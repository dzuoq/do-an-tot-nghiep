package com.example.computershop.repository;

import com.example.computershop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByResetPasswordToken(String token);

    boolean existsByPhoneNumber(String phoneNumber);

    // Thêm phương thức tìm kiếm người dùng không bị xóa
    Page<User> findByIsDeleteFalse(Pageable pageable);

    Page<User> findByIsDeleteFalseAndFullNameContainingOrEmailContainingOrPhoneNumberContaining(
            boolean isDelete, String fullName, String email, String phoneNumber, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isDelete = false")
    Long countActiveUsers();

    List<User> findByRoleIn(List<String> list);
}
