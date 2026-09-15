package com.example.api.repository;

import com.example.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // بيستخدم في التحقق من وجود العميل عند إنشاء/تعديل الأوردر
    Optional<Customer> findByNameIgnoreCase(String name);
}
