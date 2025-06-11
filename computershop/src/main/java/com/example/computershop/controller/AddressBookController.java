package com.example.computershop.controller;

import com.example.computershop.model.AddressBook;
import com.example.computershop.service.AddressBookService;
import com.example.computershop.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addressbook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    // API lấy AddressBook theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressBook>>> getAddressBookByUserId(@PathVariable Long userId) {
        List<AddressBook> addressBooks = addressBookService.getAddressBooksByUserId(userId);
        ApiResponse<List<AddressBook>> response = new ApiResponse<>(200, addressBooks,
                "Lấy danh sách địa chỉ thành công");
        return ResponseEntity.ok(response);
    }

    // API tạo mới AddressBook
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AddressBook>> createAddressBook(@PathVariable Long userId,
            @RequestBody AddressBook addressBook) {
        AddressBook createdAddressBook = addressBookService.createAddressBook(userId, addressBook);
        ApiResponse<AddressBook> response = new ApiResponse<>(201, createdAddressBook, "Tạo địa chỉ mới thành công");
        return ResponseEntity.status(201).body(response);
    }

    // API cập nhật AddressBook
    @PutMapping("/{addressBookId}")
    public ResponseEntity<ApiResponse<AddressBook>> updateAddressBook(@PathVariable Long addressBookId,
            @RequestBody AddressBook addressBook) {
        AddressBook updatedAddressBook = addressBookService.updateAddressBook(addressBookId, addressBook);
        ApiResponse<AddressBook> response = new ApiResponse<>(200, updatedAddressBook, "Cập nhật địa chỉ thành công");
        return ResponseEntity.ok(response);
    }

    // API xóa AddressBook
    @DeleteMapping("/{addressBookId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddressBook(@PathVariable Long addressBookId) {
        boolean isDeleted = addressBookService.deleteAddressBook(addressBookId);
        if (isDeleted) {
            ApiResponse<Void> response = new ApiResponse<>(200, null, "Xóa địa chỉ thành công");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(404, null, "Không tìm thấy địa chỉ");
            return ResponseEntity.status(404).body(response);
        }
    }
}
