package com.example.computershop.controller;

import com.example.computershop.service.EmailService;
import com.example.computershop.service.OrderService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private OrderService orderService;

    // API để gửi email xác nhận đơn hàng
    @PostMapping("/sendOrderConfirmation")
    public ResponseEntity<String> sendOrderConfirmationEmail(
            @RequestParam String orderCode) throws MessagingException {

        // Gọi EmailService để gửi email xác nhận
        orderService.sendOrderConfirmationEmail(orderCode);

        return ResponseEntity.ok("Email xác nhận đơn hàng đã được gửi thành công!");
    }

}
