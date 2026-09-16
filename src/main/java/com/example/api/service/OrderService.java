package com.example.api.service;

import com.example.api.dto.OrderRequest;
import com.example.api.entity.Customer;
import com.example.api.entity.Inventory;
import com.example.api.entity.Order;
import com.example.api.entity.OrderStatus;
import com.example.api.entity.Product;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.CustomerRepository;
import com.example.api.repository.InventoryRepository;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * كل الميثودز هنا @Transactional: يعني لو أي خطوة فشلت في نص العملية
 * (مثلاً الأوردر اتسجل بس خصم المخزون فشل)، Spring بيعمل rollback
 * لكل حاجة تلقائيًا - العملية بتنجح كلها أو تفشل كلها، مفيش نص حل.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));
    }

    private Customer findCustomerByNameOrThrow(String customerName) {
        return customerRepository.findByNameIgnoreCase(customerName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with name '" + customerName + "' was not found. " +
                                "Please create the customer first via POST /api/customers."));
    }

    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " was not found"));
    }

    private Inventory findInventoryOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory record found for product id " + productId +
                                ". Please add inventory for this product first via POST /api/inventory."));
    }

    /**
     * القلب الحقيقي للمنطق: بيتحقق من المخزون، يخصم منه، يحسب السعر تلقائيًا،
     * ويربط الأوردر بالمنتج الصحيح. مستخدم في الإنشاء والتعديل الكامل والجزئي.
     *
     * لو الأوردر كان مرتبط بمنتج قبل كده (تعديل مش إنشاء)، بيرجّع الكمية
     * القديمة للمخزون الأول قبل ما يخصم الكمية الجديدة من المنتج الجديد.
     */
    private void applyProductAndQuantity(Order order, Long newProductId, Integer newQuantity) {
        // لو الأوردر كان له منتج وكمية قبل كده، رجّع الكمية دي للمخزون القديم الأول
        if (order.getProduct() != null && order.getQuantity() != null) {
            Inventory oldInventory = findInventoryOrThrow(order.getProduct().getId());
            oldInventory.setQuantityAvailable(oldInventory.getQuantityAvailable() + order.getQuantity());
            inventoryRepository.save(oldInventory);
        }

        Product product = findProductOrThrow(newProductId);
        Inventory inventory = findInventoryOrThrow(product.getId());

        if (inventory.getQuantityAvailable() < newQuantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product '" + product.getName() + "'. " +
                            "Available: " + inventory.getQuantityAvailable() + ", requested: " + newQuantity + ".");
        }

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - newQuantity);
        inventoryRepository.save(inventory);

        order.setProduct(product);
        order.setQuantity(newQuantity);
        order.setTotalPrice(product.getPrice() * newQuantity);
    }

    public Order createOrder(OrderRequest request) {
        Customer customer = findCustomerByNameOrThrow(request.getCustomerName());

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(request.getStatus() != null ? request.getStatus() : OrderStatus.PENDING);

        applyProductAndQuantity(order, request.getProductId(), request.getQuantity());

        return orderRepository.save(order);
    }

    /**
     * تعديل كامل (PUT) - بيستبدل كل الحقول بالقيم الجديدة المُرسلة بالكامل.
     */
    public Order updateOrderFull(Long id, OrderRequest request) {
        Order existing = getOrderById(id);

        Customer customer = findCustomerByNameOrThrow(request.getCustomerName());
        existing.setCustomer(customer);
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());

        applyProductAndQuantity(existing, request.getProductId(), request.getQuantity());

        return orderRepository.save(existing);
    }

    /**
     * تعديل جزئي (PATCH) - بيحدّث بس الحقول اللي اتبعتت.
     * الحقول المدعومة: customerName, productId, quantity, status
     */
    public Order partialUpdateOrder(Long id, Map<String, Object> updates) {
        Order existing = getOrderById(id);

        if (updates.containsKey("customerName")) {
            Customer customer = findCustomerByNameOrThrow(String.valueOf(updates.get("customerName")));
            existing.setCustomer(customer);
        }

        if (updates.containsKey("status")) {
            try {
                OrderStatus newStatus = OrderStatus.valueOf(String.valueOf(updates.get("status")).toUpperCase());
                existing.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid status value. Allowed values: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED.");
            }
        }

        boolean productOrQuantityChanged = updates.containsKey("productId") || updates.containsKey("quantity");
        if (productOrQuantityChanged) {
            Long newProductId = updates.containsKey("productId")
                    ? Long.valueOf(String.valueOf(updates.get("productId")))
                    : existing.getProduct().getId();
            Integer newQuantity = updates.containsKey("quantity")
                    ? Integer.valueOf(String.valueOf(updates.get("quantity")))
                    : existing.getQuantity();

            applyProductAndQuantity(existing, newProductId, newQuantity);
        }

        return orderRepository.save(existing);
    }

    /**
     * حذف الأوردر - بيرجّع الكمية بتاعته للمخزون تلقائيًا قبل الحذف
     * (سلوك منطقي: إلغاء الأوردر لازم يفرّج عن المخزون اللي كان محجوز ليه).
     */
    public void deleteOrder(Long id) {
        Order existing = getOrderById(id);

        Inventory inventory = findInventoryOrThrow(existing.getProduct().getId());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + existing.getQuantity());
        inventoryRepository.save(inventory);

        orderRepository.delete(existing);
    }
}
