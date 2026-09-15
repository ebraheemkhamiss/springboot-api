package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class represents the "inventory" table.
 * One-to-One with Product: each product has exactly one inventory record
 * in this simple single-warehouse design.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // العلاقة الأساسية: كل منتج له سجل مخزون واحد بس (unique = true بيفرض ده)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @NotNull(message = "Available quantity is required")
    @PositiveOrZero(message = "Available quantity cannot be negative")
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Size(max = 150, message = "Warehouse location must not exceed 150 characters")
    @Column(name = "warehouse_location")
    private String warehouseLocation;

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
