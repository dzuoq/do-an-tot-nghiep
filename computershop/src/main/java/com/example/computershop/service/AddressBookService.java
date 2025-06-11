package com.example.computershop.service;

import com.example.computershop.model.AddressBook;
import com.example.computershop.model.User;
import com.example.computershop.repository.AddressBookRepository;
import com.example.computershop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressBookService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy AddressBook theo userId
    public List<AddressBook> getAddressBooksByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(addressBookRepository::findByUser).orElse(null);
    }

    // Tạo mới AddressBook
    public AddressBook createAddressBook(Long userId, AddressBook addressBook) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            addressBook.setUser(user.get());
            return addressBookRepository.save(addressBook);
        }
        return null;
    }

    // Cập nhật AddressBook
    public AddressBook updateAddressBook(Long addressBookId, AddressBook updatedAddressBook) {
        Optional<AddressBook> existingAddressBook = addressBookRepository.findById(addressBookId);
        if (existingAddressBook.isPresent()) {
            AddressBook addressBook = existingAddressBook.get();
            addressBook.setRecipientName(updatedAddressBook.getRecipientName());
            addressBook.setPhoneNumber(updatedAddressBook.getPhoneNumber());
            addressBook.setAddress(updatedAddressBook.getAddress());
            addressBook.setWard(updatedAddressBook.getWard());
            addressBook.setDistrict(updatedAddressBook.getDistrict());
            addressBook.setCity(updatedAddressBook.getCity());
            return addressBookRepository.save(addressBook);
        }
        return null;
    }

    // Xóa AddressBook
    public boolean deleteAddressBook(Long addressBookId) {
        Optional<AddressBook> addressBook = addressBookRepository.findById(addressBookId);
        if (addressBook.isPresent()) {
            addressBookRepository.deleteById(addressBookId);
            return true;
        }
        return false;
    }
}
