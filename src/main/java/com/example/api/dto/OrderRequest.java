package com.example.api.dto;

import com.example.api.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * الشكل اللي المستخدم بيبعته فعليًا عند إنشاء/تعديل أوردر.
 *
 * ملحوظة: مفيش totalPrice هنا خالص - الـ Service هو اللي بيحسبه تلقائيًا
 * من (سعر المنتج × الكمية)، ومينفعش يتبعت من العميل.
 */
@Data
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    // اختياري عند الإنشاء - لو مبعتوش هيتحدد PENDING تلقائيًا
    private OrderStatus status;
}
