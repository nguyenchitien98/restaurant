package com.tien.controller;


import com.tien.dto.request.OrderRequest;
import com.tien.dto.response.OrderResponseDTO;
import com.tien.model.Order;
import com.tien.model.OrderDetail;
import com.tien.model.OrderStatus;
import com.tien.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{tableId}/create")
    public ResponseEntity<Order> createOrder(@PathVariable Long tableId, @RequestBody Order order) {
        Order newOrder = orderService.createOrder(tableId,order);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Order> createOrUpdateOrder(@RequestBody OrderRequest request) {
        Order newOrder = orderService.createOrUpdateOrder(
                request.getTableId(),
                request.getUserId(),
                request.getOrderDetails(),
                request.getNote()
        );
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable Long orderId) {
        List<OrderDetail> orderDetails = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully!";
    }
}
