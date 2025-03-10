package com.example.demo2.controller;

import com.example.demo2.model.DeliveryPartner;
import com.example.demo2.model.Order;
import com.example.demo2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 1. Add an Order
    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        try {
            orderService.addOrder(order);
            return ResponseEntity.ok("Order added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Add a Delivery Partner
    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId) {
        try {
            orderService.addPartner(partnerId);
            return ResponseEntity.ok("Partner added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Assign an order to a partner
    @PutMapping("/add-order-partner-pair")
    public ResponseEntity<String> assignOrderToPartner(@RequestBody Map<String, String> request) {
        String orderId = request.get("orderId");
        String partnerId = request.get("partnerId");

        if (orderId == null || partnerId == null) {
            return ResponseEntity.badRequest().body("Order ID or Partner ID is missing");
        }

        try {
            orderService.assignOrderToPartner(orderId, partnerId);
            return ResponseEntity.ok("Order assigned successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Get Order by orderId
    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 5. Get Partner by partnerId
    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<?> getPartner(@PathVariable String partnerId) {
        try {
            DeliveryPartner partner = orderService.getPartnerById(partnerId);
            return ResponseEntity.ok(partner);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 6. Get number of orders assigned to given partnerId
    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Object> getOrderCountByPartner(@PathVariable String partnerId) {

        try {
            int count = orderService.getOrderCountByPartner(partnerId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 7. Get List of all orders assigned to given partnerId
    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<String>> getOrdersByPartner(@PathVariable String partnerId) {
        try {
            List<String> orders = orderService.getOrdersByPartner(partnerId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    // 8. Get List of all orders in the system
    @GetMapping("/get-all-orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();

            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orders); // No orders found
            }

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 9. Get count of orders which are not assigned to any partner
    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getUnassignedOrderCount() {
        try {
            int count = orderService.getUnassignedOrderCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 10. Get count of orders left undelivered by partnerId after given time
    @GetMapping("/get-count-of-orders-left-after-given-time/{time}/{partnerId}")
    public ResponseEntity<Integer> getOrdersLeftAfterTime(@PathVariable String time, @PathVariable String partnerId) {
        //return ResponseEntity.ok(orderService.getOrdersLeftAfterTime(time, partnerId));
        try {
            int count = orderService.getOrdersLeftAfterTime(time, partnerId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(0);
        }
    }

    // 11. Get the time at which the last delivery is made by given partner
    @GetMapping("/get-last-delivery-time/{partnerId}")
    public ResponseEntity<String> getLastDeliveryTime(@PathVariable String partnerId) {
        try {
            String lastDeliveryTime = orderService.getLastDeliveryTime(partnerId);
            return ResponseEntity.ok(lastDeliveryTime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 12. Delete a partner and unassign orders
    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartner(@PathVariable String partnerId) {
        try {
            orderService.deletePartner(partnerId);
            return ResponseEntity.ok("Partner deleted successfully and orders unassigned");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 13. Delete an order and unassign it from partner
    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted and unassigned");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

