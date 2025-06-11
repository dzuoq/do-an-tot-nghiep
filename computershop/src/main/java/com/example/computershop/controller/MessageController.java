package com.example.computershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.computershop.dto.MessageDTO;
import com.example.computershop.dto.UserDTO;
import com.example.computershop.model.ChatRoom;
import com.example.computershop.model.Message;
import com.example.computershop.model.User;
import com.example.computershop.service.ChatRoomService;
import com.example.computershop.service.MessageService;
import com.example.computershop.service.UserService;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private UserService userService;

    @GetMapping("/chatroom/{chatRoomId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByChatRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        List<Message> messages = messageService.getMessagesByChatRoom(chatRoomId);

        // Chuyển đổi danh sách Message sang MessageDTO
        List<MessageDTO> messageDTOs = messages.stream().map(this::convertToDTO).collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping("/chatroom/{chatRoomId}")
    public ResponseEntity<Message> insertMessage(@PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam("userId") Long userId, // Nhận userId từ request param
            @RequestBody Message message) {
        // Tìm ChatRoom dựa vào chatRoomId
        ChatRoom chatRoom = chatRoomService.findById(chatRoomId);
        message.setChatRoom(chatRoom);

        // Tìm User (người gửi) từ cơ sở dữ liệu
        User sender = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));
        message.setSender(sender);

        // Đặt thời gian gửi tin nhắn
        message.setTimestamp(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        Message savedMessage = messageService.insertMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    private MessageDTO convertToDTO(Message message) {
        UserDTO senderDTO = new UserDTO(
                message.getSender().getUserId(),
                message.getSender().getUsername(),
                message.getSender().getEmail(),
                message.getSender().getPhoneNumber(),
                message.getSender().getFullName(),
                message.getSender().getAvatar());

        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getTimestamp(),
                senderDTO,
                message.getChatRoom().getId() // Chỉ lấy chatRoomId
        );
    }
}
