package com.example.api.repository;

import com.example.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JpaRepository بيدّينا تلقائيًا:
 * save(), findById(), findAll(), deleteById(), إلخ
 * من غير ما نكتب أي كود SQL.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Query method تلقائي: Spring Data بيبني الاستعلام من اسم الميثود نفسه
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceLessThanEqual(Double price);
}
