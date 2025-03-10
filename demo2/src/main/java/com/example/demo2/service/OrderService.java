package com.example.demo2.service;

import com.example.demo2.model.DeliveryPartner;
import com.example.demo2.model.Order;
import com.example.demo2.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public void addOrder(Order order) {

        if (order == null || order.getOrderId() == null || order.getOrderId().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        // Validate if order already exists
        if (orderRepository.orderExists(order.getOrderId())) {
            throw new IllegalArgumentException("Order with ID " + order.getOrderId() + " already exists");
        }

        orderRepository.addOrder(order);
    }

    public void addPartner(String partnerId) {
        if (partnerId == null || partnerId.isEmpty()) {
            throw new IllegalArgumentException("Partner ID cannot be null or empty");
        }

        if (orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner with ID " + partnerId + " already exists");
        }

        orderRepository.addPartner(partnerId);
    }

    public void assignOrderToPartner(String orderId, String partnerId) {

        if (!orderRepository.orderExists(orderId)) {
            throw new IllegalArgumentException("Order ID " + orderId + " not found");
        }
        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner ID " + partnerId + " not found");
        }

        orderRepository.assignOrderToPartner(orderId, partnerId);
    }

    public Order getOrderById(String orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found");
        }
        return order;
    }

    public DeliveryPartner getPartnerById(String partnerId) {

//        DeliveryPartner partner = orderRepository.getPartnerById(partnerId);
//        if (partner == null) return null;
//
//        // Fetch assigned orders dynamically
//        List<String> assignedOrders = orderRepository.getOrdersByPartnerId(partnerId);
//        partner.setAssignedOrders(assignedOrders);
//
//        return partner;

        DeliveryPartner partner = orderRepository.getPartnerById(partnerId);
        if (partner == null) {
            throw new IllegalArgumentException("Partner with ID " + partnerId + " not found");
        }
        return partner;
    }

    public Integer getOrderCountByPartner(String partnerId) {
        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner ID " + partnerId + " not found");
        }
        return orderRepository.getOrderCountByPartner(partnerId);
    }

    public List<String> getOrdersByPartner(String partnerId) {
        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner ID " + partnerId + " not found");
        }
        return orderRepository.getOrdersByPartner(partnerId);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.getAllOrders();

        if (orders == null) {
            throw new IllegalStateException("Order repository returned null");
        }

        return orders;

    }

    public int getUnassignedOrderCount() {
        return orderRepository.getUnassignedOrderCount();
    }

    public int getOrdersLeftAfterTime(String time, String partnerId) {

        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner with ID " + partnerId + " not found");
        }

        LocalTime givenTime;
        try {
            givenTime = LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Expected HH:MM (24-hour format)");
        }

        return orderRepository.getOrdersLeftAfterTime(givenTime, partnerId);
    }

    public String getLastDeliveryTime(String partnerId) {
        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner with ID " + partnerId + " not found");
        }

        String lastDeliveryTime = orderRepository.getLastDeliveryTime(partnerId);
        if (lastDeliveryTime == null) {
            throw new IllegalArgumentException("No deliveries found for partner ID " + partnerId);
        }

        return lastDeliveryTime;
    }

    public void deletePartner(String partnerId) {
        if (!orderRepository.partnerExists(partnerId)) {
            throw new IllegalArgumentException("Partner with " + partnerId + " does not exist");
        }
        orderRepository.deletePartner(partnerId);
    }

    public void deleteOrder(String orderId) {
        if (!orderRepository.orderExists(orderId)) {
            throw new IllegalArgumentException("Order with " + orderId + " does not exist");
        }
        orderRepository.deleteOrder(orderId);
    }
}