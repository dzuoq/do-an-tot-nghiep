package com.example.computershop.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.example.computershop.model.Message;

@Controller
public class WebSocketMessageController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chatroom")
    public Message sendMessage(Message message) {
        // Xử lý logic nếu cần thiết
        return message; // Trả về tin nhắn để phát lại cho các subscriber
    }
}
