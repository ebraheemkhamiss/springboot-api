package com.example.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotNull(message = "Available quantity is required")
    @PositiveOrZero(message = "Available quantity cannot be negative")
    private Integer quantityAvailable;

    private String warehouseLocation;
}
