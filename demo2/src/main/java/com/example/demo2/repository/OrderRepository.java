package com.example.demo2.repository;

import com.example.demo2.model.DeliveryPartner;
import com.example.demo2.model.Order;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Repository
public class OrderRepository {
    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, DeliveryPartner> partners = new HashMap<>();
    private final Map<String, String> orderPartnerMap = new HashMap<>();
    private final Map<String, List<String>> partnerOrderMap = new HashMap<>();

    public boolean orderExists(String orderId) {
        return orders.containsKey(orderId);
    }

    public boolean partnerExists(String partnerId) {
        return partners.containsKey(partnerId);
    }
//---------------

    public void addOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public void addPartner(String partnerId) {
        partners.put(partnerId, new DeliveryPartner(partnerId, new ArrayList<>()));
    }


    public void assignOrderToPartner(String orderId, String partnerId) {

        // Assign order to partner
        orderPartnerMap.put(orderId, partnerId);

        // Update partner's order list
        partnerOrderMap.putIfAbsent(partnerId, new ArrayList<>());
        partnerOrderMap.get(partnerId).add(orderId);

        // Update DeliveryPartner object
        DeliveryPartner partner = partners.get(partnerId);
        if (partner != null) {
            partner.getAssignedOrders().add(orderId);
        }
    }


    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partners.get(partnerId);
    }

    public Integer getOrderCountByPartner(String partnerId) {
        return partnerOrderMap.getOrDefault(partnerId, Collections.emptyList()).size();
    }

    public List<String> getOrdersByPartner(String partnerId) {
       return partnerOrderMap.getOrDefault(partnerId, Collections.emptyList());
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public int getUnassignedOrderCount() {
        return (int) orders.keySet().stream().filter(orderId -> !orderPartnerMap.containsKey(orderId)).count();
    }


    public int getOrdersLeftAfterTime(LocalTime givenTime, String partnerId) {
        return (int) partnerOrderMap.getOrDefault(partnerId, Collections.emptyList()).stream()
                .map(orders::get)
                .filter(order -> order != null && order.getDeliveryTime() != null &&
                        LocalTime.parse(order.getDeliveryTime()).isAfter(givenTime))
                .count();
    }


    public String getLastDeliveryTime(String partnerId) {
//        return partnerOrderMap.getOrDefault(partnerId, Collections.emptyList()).stream()
//                .map(orders::get)
//                .map(Order::getDeliveryTime)
//                .max(String::compareTo)
//                .orElse("No deliveries made");
        return partnerOrderMap.getOrDefault(partnerId, Collections.emptyList()).stream()
                .map(orders::get)
                .filter(Objects::nonNull)  // Remove potential null values
                .map(Order::getDeliveryTime)
                .map(time -> {
                    try {
                        return LocalTime.parse(time);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid time format for an order");
                    }
                })
                .max(LocalTime::compareTo)
                .map(LocalTime::toString)
                .orElse(null); // Returns null if no orders are found
    }

    public void deletePartner(String partnerId) {
        // Removing assigned orders
        List<String> assignedOrders = partnerOrderMap.remove(partnerId);
        if (assignedOrders != null) {
            assignedOrders.forEach(orderPartnerMap::remove);
        }

        // Removing partner
        partners.remove(partnerId);
    }


    public void deleteOrder(String orderId) {

        // Remove the order-partner mapping
        String partnerId = orderPartnerMap.remove(orderId);

        if (partnerId != null && partnerOrderMap.containsKey(partnerId)) {
            List<String> ordersList = partnerOrderMap.get(partnerId);
            ordersList.remove(orderId);

            // If partner has no more orders, remove them from the map
            if (ordersList.isEmpty()) {
                partnerOrderMap.remove(partnerId);
            }
        }

        // Remove the order from orders map
        orders.remove(orderId);
    }

}
