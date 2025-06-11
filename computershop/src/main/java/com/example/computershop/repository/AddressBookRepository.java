package com.example.computershop.repository;

import com.example.computershop.model.AddressBook;
import com.example.computershop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    List<AddressBook> findByUser(User user);
}
