package com.example.api.controller;

import com.example.api.dto.OrderRequest;
import com.example.api.entity.Order;
import com.example.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    @Operation(summary = "Get all orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get a single order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Create a new order",
            description = "'customerName' must match an EXISTING customer (create it first via POST /api/customers). " +
                    "'productId' must match an existing product that has an inventory record. " +
                    "The system automatically checks stock, deducts the ordered quantity from inventory, " +
                    "and calculates totalPrice = product price * quantity. " +
                    "If stock is insufficient, the request is rejected with 400 and nothing is changed.")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Customer, product, or inventory record not found")
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        Order saved = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Fully update an existing order",
            description = "Replaces ALL fields with the new values sent. Stock is automatically restored " +
                    "for the old product/quantity before being deducted again for the new values.")
    @ApiResponse(responseCode = "200", description = "Updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Order, customer, or product not found")
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrderFull(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrderFull(id, request));
    }

    @Operation(summary = "Partially update an order",
            description = "Updates only the fields included in the request body. " +
                    "Supported fields: customerName, productId, quantity, status. " +
                    "Changing productId or quantity automatically re-validates and adjusts inventory, " +
                    "and recalculates totalPrice.")
    @ApiResponse(responseCode = "200", description = "Updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Order, customer, or product not found")
    @PatchMapping("/{id}")
    public ResponseEntity<Order> partialUpdateOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Only include the fields you want to change",
                    content = @Content(examples = {
                            @ExampleObject(name = "Update status only",
                                    value = "{\"status\": \"SHIPPED\"}"),
                            @ExampleObject(name = "Update customer",
                                    value = "{\"customerName\": \"Ahmed Mohamed\"}"),
                            @ExampleObject(name = "Update quantity",
                                    value = "{\"quantity\": 3}")
                    })
            )
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(orderService.partialUpdateOrder(id, updates));
    }

    @Operation(summary = "Delete an order",
            description = "Automatically restores the ordered quantity back to inventory before deleting.")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
