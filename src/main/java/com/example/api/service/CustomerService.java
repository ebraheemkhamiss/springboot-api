package com.example.api.service;

import com.example.api.entity.Customer;
import com.example.api.entity.Order;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repository.CustomerRepository;
import com.example.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " was not found"));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);
        existing.setName(updatedCustomer.getName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setAddress(updatedCustomer.getAddress());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long id) {
        Customer existing = getCustomerById(id);
        customerRepository.delete(existing);
    }

    // بيستخدم في GET /api/customers/{id}/orders
    public List<Order> getOrdersForCustomer(Long customerId) {
        // بيتأكد الأول إن العميل موجود أصلاً، وإلا يرجّع 404 واضح
        getCustomerById(customerId);
        return orderRepository.findByCustomer_Id(customerId);
    }
}
