package com.example.api.entity;

/**
 * الحالات المسموح بها فقط لحالة الطلب.
 * أي قيمة تانية غير دي هترفض تلقائيًا برسالة خطأ واضحة.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
