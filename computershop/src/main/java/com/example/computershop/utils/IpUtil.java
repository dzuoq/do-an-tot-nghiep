package com.example.computershop.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    // Phương thức lấy địa chỉ IP từ yêu cầu HTTP
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
