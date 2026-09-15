package com.example.api.service;

import com.example.api.dto.InventoryRequest;
import com.example.api.entity.Inventory;
import com.example.api.entity.Product;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.InventoryRepository;
import com.example.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record with id " + id + " was not found"));
    }

    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record found for product id " + productId));
    }

    public Inventory createInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with id " + request.getProductId() + " was not found"));

        // العلاقة 1:1 - مينفعش يكون فيه أكتر من سجل مخزون لنفس المنتج
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Inventory record already exists for product id " + request.getProductId());
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantityAvailable(request.getQuantityAvailable());
        inventory.setWarehouseLocation(request.getWarehouseLocation());
        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(Long id, Inventory updatedInventory) {
        Inventory existing = getInventoryById(id);
        existing.setQuantityAvailable(updatedInventory.getQuantityAvailable());
        existing.setWarehouseLocation(updatedInventory.getWarehouseLocation());
        return inventoryRepository.save(existing);
    }

    public void deleteInventory(Long id) {
        Inventory existing = getInventoryById(id);
        inventoryRepository.delete(existing);
    }
}
