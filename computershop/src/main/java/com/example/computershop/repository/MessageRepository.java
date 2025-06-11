package com.example.computershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.computershop.model.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(Long chatRoomId);
}
