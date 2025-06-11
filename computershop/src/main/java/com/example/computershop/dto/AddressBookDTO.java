package com.example.computershop.dto;

public class AddressBookDTO {
    private Long addressBookId;
    private String recipientName;
    private String phoneNumber;
    private String address;
    private String ward;
    private String district;
    private String city;
    private String email; // Thêm trường email

    public AddressBookDTO() {
    }

    public AddressBookDTO(Long addressBookId, String recipientName, String phoneNumber, String address, String ward,
            String district, String city, String email) { // Cập nhật constructor để thêm email
        this.addressBookId = addressBookId;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.email = email; // Gán giá trị email
    }

    public Long getAddressBookId() {
        return addressBookId;
    }

    public void setAddressBookId(Long addressBookId) {
        this.addressBookId = addressBookId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() { // Getter cho email
        return email;
    }

    public void setEmail(String email) { // Setter cho email
        this.email = email;
    }
}
