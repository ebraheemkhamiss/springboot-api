package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * هذا الكلاس يمثل جدول "products" في قاعدة البيانات.
 * غيّر الحقول دي لأي بيانات محتاج تخزنها في مشروعك.
 */
@Entity
@Table(name = "products")
@Data // تولّد getters/setters/toString تلقائيًا (من Lombok)
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "اسم المنتج مطلوب")
    @Column(nullable = false)
    private String name;

    private String description;

    @Positive(message = "السعر لازم يكون رقم موجب")
    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
