package com.example.computershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.computershop.model.ChatRoom;
import com.google.common.base.Optional;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Sửa lại phương thức này để truy vấn theo thuộc tính userId của customer
    List<ChatRoom> findByCustomer_UserId(Long customerId);

    // Truy vấn theo userId của participants
    List<ChatRoom> findByParticipants_UserId(Long userId);

    // Thêm phương thức tùy chỉnh trong ChatRoomRepository
    @Query("SELECT c FROM ChatRoom c WHERE c.customer.userId = :customerId")
    Optional<ChatRoom> findChatRoomByCustomerId(@Param("customerId") Long customerId);

}
