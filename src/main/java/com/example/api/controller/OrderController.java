package com.example.api.controller;

import com.example.api.entity.Order;
import com.example.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Base URL: http://localhost:8080/api/orders
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Manage and store order data")
public class OrderController {

    private final OrderService orderService;

    // 5. عرض كل الـ orders
    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 4. عرض order معين عن طريق id
    @Operation(summary = "Get a single order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 1. إضافة order جديد
    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order saved = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 2. تعديل order موجود بالكامل (PUT)
    @Operation(summary = "Fully update an existing order",
            description = "Replaces ALL fields with the new values sent. Any field not included will be lost.")
    @ApiResponse(responseCode = "200", description = "Updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrderFull(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateOrderFull(id, order));
    }

    // 3. تعديل جزئي (PATCH)
    @Operation(summary = "Partially update an order",
            description = "Updates only the fields included in the request body. " +
                    "Example: sending {\"status\": \"SHIPPED\"} updates only the status " +
                    "and leaves all other fields untouched. " +
                    "Allowed status values: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED.")
    @ApiResponse(responseCode = "200", description = "Updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PatchMapping("/{id}")
    public ResponseEntity<Order> partialUpdateOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(orderService.partialUpdateOrder(id, updates));
    }

    // 6. حذف order
    @Operation(summary = "Delete an order")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
