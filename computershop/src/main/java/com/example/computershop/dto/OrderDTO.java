package com.example.computershop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long orderId;
    private String code;
    private LocalDateTime date;
    private String note;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private BigDecimal discount;

    private UserDTO user;
    private AddressBookDTO addressBook;
    private String status;
    private List<OrderDetailDTO> orderDetails;

    public OrderDTO() {
    }

    public OrderDTO(Long orderId, String code, LocalDateTime date, String note, String paymentMethod,
            BigDecimal totalPrice, BigDecimal discount, UserDTO user, AddressBookDTO addressBook, String status,
            List<OrderDetailDTO> orderDetails) {
        this.orderId = orderId;
        this.code = code;
        this.date = date;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.user = user;
        this.addressBook = addressBook;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public AddressBookDTO getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(AddressBookDTO addressBook) {
        this.addressBook = addressBook;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    // Getters and setters...
}
