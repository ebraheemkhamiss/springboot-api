package com.example.api.controller;

import com.example.api.entity.Product;
import com.example.api.service.ProductService;
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
 * الـ Controller ده مسؤول عن استقبال طلبات HTTP وإرجاع الردود.
 * Base URL: http://localhost:8080/api/products
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "إدارة وتخزين بيانات المنتجات")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "جلب كل المنتجات", description = "بيرجّع قائمة بكل المنتجات المخزنة في قاعدة البيانات")
    @ApiResponse(responseCode = "200", description = "تم الجلب بنجاح")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "جلب منتج واحد بالـ ID")
    @ApiResponse(responseCode = "200", description = "تم العثور على المنتج")
    @ApiResponse(responseCode = "404", description = "المنتج غير موجود")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "رقم المنتج", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "البحث عن منتجات بالاسم", description = "بحث جزئي غير حساس لحالة الأحرف")
    @ApiResponse(responseCode = "200", description = "تم البحث بنجاح")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @Parameter(description = "جزء من اسم المنتج", example = "لابتوب") @RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @Operation(summary = "إضافة منتج جديد", description = "بيستقبل بيانات المنتج ويخزّنها في قاعدة البيانات")
    @ApiResponse(responseCode = "201", description = "تم إنشاء المنتج بنجاح")
    @ApiResponse(responseCode = "400", description = "بيانات غير صحيحة")
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product saved = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "تحديث منتج موجود")
    @ApiResponse(responseCode = "200", description = "تم التحديث بنجاح")
    @ApiResponse(responseCode = "404", description = "المنتج غير موجود")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "رقم المنتج") @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @Operation(summary = "حذف منتج")
    @ApiResponse(responseCode = "204", description = "تم الحذف بنجاح")
    @ApiResponse(responseCode = "404", description = "المنتج غير موجود")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "رقم المنتج") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
