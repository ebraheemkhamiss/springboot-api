package com.example.api.service;

import com.example.api.entity.Order;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    // Spring Boot يوفر ObjectMapper جاهز تلقائيًا نستخدمه لدمج التعديلات الجزئية
    private final ObjectMapper objectMapper;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * تعديل كامل (PUT) - بيستبدل كل الحقول بالقيم الجديدة المُرسلة بالكامل.
     */
    public Order updateOrderFull(Long id, Order updatedOrder) {
        Order existing = getOrderById(id);
        existing.setCustomerName(updatedOrder.getCustomerName());
        existing.setProductName(updatedOrder.getProductName());
        existing.setQuantity(updatedOrder.getQuantity());
        existing.setTotalPrice(updatedOrder.getTotalPrice());
        existing.setStatus(updatedOrder.getStatus());
        return orderRepository.save(existing);
    }

    /**
     * تعديل جزئي (PATCH) - بيحدّث بس الحقول اللي اتبعتت في الطلب،
     * وبيسيب باقي الحقول زي ما هي من غير ما يلمسها.
     *
     * مثال: لو بعتّ بس {"status": "SHIPPED"}، هيتغير الـ status بس
     * وباقي بيانات الأوردر (اسم العميل، الكمية، السعر...) هتفضل زي ما هي.
     */
    public Order partialUpdateOrder(Long id, Map<String, Object> updates) {
        Order existing = getOrderById(id);
        try {
            // Jackson بيدمج القيم الموجودة في الـ Map فوق الكائن الحالي، ويسيب أي حقل مش مذكور زي ما هو
            objectMapper.updateValue(existing, updates);
        } catch (JsonMappingException e) {
            // بيحصل مثلاً لو حد بعت قيمة status مش من ضمن القيم المسموحة
            throw new IllegalArgumentException(
                    "Invalid value in request body: " + e.getOriginalMessage(), e);
        }
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        Order existing = getOrderById(id);
        orderRepository.delete(existing);
    }
}
