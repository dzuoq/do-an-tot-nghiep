package com.example.computershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.computershop.dto.ChatRoomDTO;
import com.example.computershop.dto.MessageDTO;
import com.example.computershop.dto.UserDTO;
import com.example.computershop.model.ChatRoom;
import com.example.computershop.service.ChatRoomService;
import com.google.common.base.Optional;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

        @Autowired
        private ChatRoomService chatRoomService;

        // Tạo phòng chat giữa admin, staff và khách hàng
        @PostMapping
        public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestParam("customerId") Long customerId) {
                // Kiểm tra nếu đã có phòng chat cho customerId
                Optional<ChatRoom> existingChatRoom = chatRoomService.findByCustomerId(customerId);

                if (existingChatRoom.isPresent()) {
                        // Nếu đã tồn tại phòng chat, chuyển đổi sang DTO và trả về phòng chat hiện có
                        ChatRoomDTO chatRoomDTO = convertToDTO(existingChatRoom.get());
                        return ResponseEntity.ok(chatRoomDTO);
                }

                // Nếu chưa có phòng chat, tạo mới
                ChatRoom newChatRoom = chatRoomService.createChatRoom(customerId);
                ChatRoomDTO newChatRoomDTO = convertToDTO(newChatRoom);
                return ResponseEntity.ok(newChatRoomDTO);
        }

        // Lấy danh sách phòng chat của khách hàng
        @GetMapping("/customer/{customerId}")
        public ResponseEntity<List<ChatRoomDTO>> getChatRoomsForCustomer(@PathVariable("customerId") Long customerId) {
                List<ChatRoom> chatRooms = chatRoomService.getChatRoomsForCustomer(customerId);

                // Chuyển đổi danh sách ChatRoom sang ChatRoomDTO
                List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream().map(this::convertToDTO)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(chatRoomDTOs);
        }

        @GetMapping("/admin")
        public ResponseEntity<List<ChatRoomDTO>> getChatRoomsForAdminAndStaff(@RequestParam("userId") Long userId) {
                List<ChatRoom> chatRooms = chatRoomService.getChatRoomsForAdminAndStaff(userId);

                // Chuyển đổi danh sách ChatRoom sang ChatRoomDTO
                List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream().map(this::convertToDTO)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(chatRoomDTOs);
        }

        // Hàm chuyển đổi từ ChatRoom sang ChatRoomDTO
        private ChatRoomDTO convertToDTO(ChatRoom chatRoom) {
                // Chuyển đổi danh sách User sang UserDTO
                List<UserDTO> participantDTOs = chatRoom.getParticipants().stream()
                                .map(user -> new UserDTO(
                                                user.getUserId(),
                                                user.getUsername(),
                                                user.getEmail(),
                                                user.getPhoneNumber(),
                                                user.getFullName(),
                                                user.getAvatar()))
                                .collect(Collectors.toList());

                // Chuyển đổi khách hàng (customer) sang UserDTO
                UserDTO customerDTO = new UserDTO(
                                chatRoom.getCustomer().getUserId(),
                                chatRoom.getCustomer().getUsername(),
                                chatRoom.getCustomer().getEmail(),
                                chatRoom.getCustomer().getPhoneNumber(),
                                chatRoom.getCustomer().getFullName(),
                                chatRoom.getCustomer().getAvatar());

                // Chuyển đổi danh sách tin nhắn (messages) sang MessageDTO
                List<MessageDTO> messageDTOs = chatRoom.getMessages().stream()
                                .map(message -> new MessageDTO(
                                                message.getId(),
                                                message.getContent(),
                                                message.getTimestamp(),
                                                new UserDTO(
                                                                message.getSender().getUserId(),
                                                                message.getSender().getUsername(),
                                                                message.getSender().getEmail(),
                                                                message.getSender().getPhoneNumber(),
                                                                message.getSender().getFullName(),
                                                                message.getSender().getAvatar()),
                                                message.getChatRoom().getId()))
                                .collect(Collectors.toList());

                // Trả về ChatRoomDTO
                return new ChatRoomDTO(
                                chatRoom.getId(),
                                participantDTOs,
                                customerDTO,
                                messageDTOs);
        }
}
