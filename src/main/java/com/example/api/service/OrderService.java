package com.example.api.service;

import com.example.api.dto.OrderRequest;
import com.example.api.entity.Customer;
import com.example.api.entity.Order;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.CustomerRepository;
import com.example.api.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    // Spring Boot يوفر ObjectMapper جاهز تلقائيًا نستخدمه لدمج التعديلات الجزئية
    private final ObjectMapper objectMapper;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));
    }

    /**
     * بيدوّر على العميل بالاسم بالظبط. لو مش موجود، بيرمي 404 - مفيش إنشاء
     * تلقائي للعميل، الاسم لازم يكون موجود فعليًا في جدول customers.
     */
    private Customer findCustomerByNameOrThrow(String customerName) {
        return customerRepository.findByNameIgnoreCase(customerName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with name '" + customerName + "' was not found. " +
                                "Please create the customer first via POST /api/customers."));
    }

    public Order createOrder(OrderRequest request) {
        Customer customer = findCustomerByNameOrThrow(request.getCustomerName());

        Order order = new Order();
        order.setCustomer(customer);
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        return orderRepository.save(order);
    }

    /**
     * تعديل كامل (PUT) - بيستبدل كل الحقول بالقيم الجديدة المُرسلة بالكامل.
     */
    public Order updateOrderFull(Long id, OrderRequest request) {
        Order existing = getOrderById(id);
        Customer customer = findCustomerByNameOrThrow(request.getCustomerName());

        existing.setCustomer(customer);
        existing.setProductName(request.getProductName());
        existing.setQuantity(request.getQuantity());
        existing.setTotalPrice(request.getTotalPrice());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
        return orderRepository.save(existing);
    }

    /**
     * تعديل جزئي (PATCH) - بيحدّث بس الحقول اللي اتبعتت في الطلب.
     * لو "customerName" ضمن الحقول المُرسلة، بيتحقق منه بنفس الشرط
     * (لازم يكون عميل موجود فعلاً) قبل ما يربطه بالأوردر.
     */
    public Order partialUpdateOrder(Long id, Map<String, Object> updates) {
        Order existing = getOrderById(id);

        // customerName مش فيلد حقيقي في Order بقى (بقى customer كـ object)
        // فلازم نتعامل معاه لوحده قبل ما نسيب Jackson يدمج الباقي تلقائيًا
        Map<String, Object> remainingUpdates = new HashMap<>(updates);
        Object customerNameValue = remainingUpdates.remove("customerName");
        if (customerNameValue != null) {
            Customer customer = findCustomerByNameOrThrow(customerNameValue.toString());
            existing.setCustomer(customer);
        }

        if (!remainingUpdates.isEmpty()) {
            try {
                // Jackson بيدمج القيم الموجودة في الـ Map فوق الكائن الحالي
                objectMapper.updateValue(existing, remainingUpdates);
            } catch (JsonMappingException e) {
                // بيحصل مثلاً لو حد بعت قيمة status مش من ضمن القيم المسموحة
                throw new IllegalArgumentException(
                        "Invalid value in request body: " + e.getOriginalMessage(), e);
            }
        }

        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        Order existing = getOrderById(id);
        orderRepository.delete(existing);
    }
}
