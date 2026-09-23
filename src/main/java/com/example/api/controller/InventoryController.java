package com.example.api.controller;

import com.example.api.dto.InventoryRequest;
import com.example.api.entity.Inventory;
import com.example.api.service.InventoryService;
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

/**
 * Base URL: http://localhost:8080/api/inventory
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Manage product stock levels")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get all inventory records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @Operation(summary = "Get a single inventory record by ID")
    @ApiResponse(responseCode = "200", description = "Inventory record found")
    @ApiResponse(responseCode = "404", description = "Inventory record not found")
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(
            @Parameter(description = "Inventory record ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @Operation(summary = "Get the inventory record for a specific product")
    @ApiResponse(responseCode = "200", description = "Inventory record found")
    @ApiResponse(responseCode = "404", description = "No inventory record for this product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @Operation(summary = "Create an inventory record for a product",
            description = "Each product can have only ONE inventory record (one-to-one relationship).")
    @ApiResponse(responseCode = "201", description = "Inventory record created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "409", description = "An inventory record already exists for this product")
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@Valid @RequestBody InventoryRequest request) {
        Inventory saved = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Update an inventory record")
    @ApiResponse(responseCode = "200", description = "Updated successfully")
    @ApiResponse(responseCode = "404", description = "Inventory record not found")
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @Parameter(description = "Inventory record ID") @PathVariable Long id,
            @Valid @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, inventory));
    }

    @Operation(summary = "Delete an inventory record")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @ApiResponse(responseCode = "404", description = "Inventory record not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Inventory record ID") @PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
