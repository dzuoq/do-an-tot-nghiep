package com.example.computershop.repository;

import com.example.computershop.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Tìm tất cả các bình luận của một sản phẩm
    List<Comment> findByProductProductId(Long productId);

    // Tìm tất cả các bình luận của một người dùng
    List<Comment> findByUserUserId(Long userId);
}
