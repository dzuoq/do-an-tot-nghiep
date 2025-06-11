package com.example.computershop.dto;

import java.util.List;

public class ChatRoomDTO {
    private Long id;
    private List<UserDTO> participants; // Danh sách các user tham gia
    private UserDTO customer; // Thông tin khách hàng
    private List<MessageDTO> messages; // Danh sách tin nhắn dưới dạng MessageDTO

    public ChatRoomDTO() {
    }

    public ChatRoomDTO(Long id, List<UserDTO> participants, UserDTO customer, List<MessageDTO> messages) {
        this.id = id;
        this.participants = participants;
        this.customer = customer;
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }

    public UserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(UserDTO customer) {
        this.customer = customer;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
