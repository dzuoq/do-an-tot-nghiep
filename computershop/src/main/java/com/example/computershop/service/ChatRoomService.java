package com.example.computershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.computershop.model.ChatRoom;
import com.example.computershop.model.User;
import com.example.computershop.repository.ChatRoomRepository;
import com.example.computershop.repository.UserRepository;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo phòng chat giữa admin, staff và khách hàng
    public ChatRoom createChatRoom(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        // Tìm tất cả user có role là ADMIN hoặc STAFF
        List<User> adminsAndStaff = userRepository.findByRoleIn(List.of("ADMIN", "STAFF"));

        // Thêm customer vào participants cùng với admin và staff
        List<User> participants = new ArrayList<>(adminsAndStaff);
        participants.add(customer);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCustomer(customer);
        chatRoom.setParticipants(participants); // Đảm bảo khách hàng và quản lý đều trong danh sách

        return chatRoomRepository.save(chatRoom);
    }

    // Lấy danh sách phòng chat của khách hàng
    public List<ChatRoom> getChatRoomsForCustomer(Long customerId) {
        return chatRoomRepository.findByCustomer_UserId(customerId);
    }

    public List<ChatRoom> getChatRoomsForAdminAndStaff(Long userId) {
        return chatRoomRepository.findByParticipants_UserId(userId);
    }

    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Phòng chat không tồn tại"));
    }
    public Optional<ChatRoom> findByCustomerId(Long customerId) {
        return chatRoomRepository.findChatRoomByCustomerId(customerId);
    }
}
