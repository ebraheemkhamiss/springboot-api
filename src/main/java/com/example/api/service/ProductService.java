package com.example.api.service;

import com.example.api.entity.Product;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * طبقة الـ Service بتحتوي منطق العمل، وبتفصل الـ Controller عن التعامل المباشر
 * مع الـ Repository. ده بيخلي الكود أنظف وأسهل في الاختبار (testing).
 */
@Service
@RequiredArgsConstructor // Lombok بيعمل constructor للـ final fields (dependency injection)
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("المنتج برقم " + id + " غير موجود"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        productRepository.delete(existing);
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
