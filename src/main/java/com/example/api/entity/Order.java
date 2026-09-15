package com.example.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class represents the "orders" table in the database.
 *
 * NOTE ON RELATIONSHIPS:
 * - customer: @ManyToOne to Customer. This is unidirectional (Customer does
 *   NOT hold a list of orders) to keep JSON serialization simple and avoid
 *   circular references.
 * - productName remains a plain string for now (no FK to Product yet).
 *   Introducing a real Order -> OrderItem -> Product relationship is a
 *   separate, larger change (needed to support multiple products per order
 *   and to link into Inventory).
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // العلاقة الحقيقية بالعميل - Many orders can belong to one Customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    @Column(name = "product_name", nullable = false)
    private String productName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be a positive number")
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // Only PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED are accepted
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

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
