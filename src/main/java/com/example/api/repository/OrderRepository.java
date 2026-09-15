package com.example.api.repository;

import com.example.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // بيستخدم في GET /api/customers/{id}/orders
    List<Order> findByCustomer_Id(Long customerId);
}
