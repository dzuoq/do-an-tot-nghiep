package com.example.computershop.repository;

import com.example.computershop.model.New;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NewRepository extends JpaRepository<New, Long> {
    Page<New> findByIsDeletedFalse(Pageable pageable);

    Page<New> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);
}
