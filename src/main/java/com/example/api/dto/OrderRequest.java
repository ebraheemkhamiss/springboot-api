package com.example.api.dto;

import com.example.api.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * الشكل اللي المستخدم بيبعته فعليًا عند إنشاء/تعديل أوردر.
 * بيستقبل "customerName" كنص بسيط، والـ Service هو المسؤول عن البحث
 * عن الـ Customer المطابق في قاعدة البيانات وربطه بالأوردر.
 */
@Data
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be a positive number")
    private Double totalPrice;

    // اختياري عند الإنشاء - لو مبعتوش هيتحدد PENDING تلقائيًا
    private OrderStatus status;
}
