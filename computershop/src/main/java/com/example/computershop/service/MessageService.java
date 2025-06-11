package com.example.computershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.computershop.model.ChatRoom;
import com.example.computershop.model.Message;
import com.example.computershop.model.User;
import com.example.computershop.repository.ChatRoomRepository;
import com.example.computershop.repository.MessageRepository;
import com.example.computershop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    // Gửi tin nhắn từ user tới phòng chat
    public Message sendMessage(Long chatRoomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chat"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        if (!chatRoom.getParticipants().contains(sender) && !chatRoom.getCustomer().equals(sender)) {
            throw new RuntimeException("Bạn không có quyền tham gia phòng chat này");
        }

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message); // Save message to the database
    }

    public List<Message> getMessagesByChatRoom(Long chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    public Message insertMessage(Message message) {
        return messageRepository.save(message);
    }
}
