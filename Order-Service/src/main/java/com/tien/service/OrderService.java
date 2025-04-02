package com.tien.service;

import com.tien.client.MenuClient;
import com.tien.kafka.OrderKafkaProducer;
import com.tien.model.*;
import com.tien.repository.OrderDetailRepository;
import com.tien.repository.OrderRepository;
import com.tien.websocket.OrderWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final TableService tableService;
    private final MenuClient menuClient;
    private final OrderWebSocketService orderWebSocketService;
    private final OrderKafkaProducer orderKafkaProducer;


    // Tạo mới order cho bàn
    public Order createOrder(Long userId ,Long tableId, List<OrderDetail> orderDetails) {
        RestaurantTable table = tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
        for (OrderDetail orderDetail : orderDetails){
            Menu item = menuClient.getMenuById(orderDetail.getMenuId());
            if (item == null){
                throw new RuntimeException("Menu item not found: " + orderDetail.getMenuId());
            }
        }

        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.PENDING);
        order.setUserId(userId);
        order.setOrderDetails(orderDetails);
        order.setTotalPrice(orderDetails.stream().mapToDouble(od -> od.getPrice() * od.getQuantity()).sum());

        return orderRepository.save(order);
    }

    // Lấy chi tiết order của bàn
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    // Cập nhật trạng thái bàn sau khi gọi món
    public RestaurantTable updateTableStatusAfterOrder(Long tableId) {
        tableService.updateTableStatus(tableId, TableStatus.OCCUPIED);
        return tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);

            // Gửi sự kiện WebSocket
            orderWebSocketService.sendOrderUpdate(updatedOrder);

            // Gửi sự kiện Kafka đến Kitchen Service
            orderKafkaProducer.sendOrderStatusUpdate(updatedOrder);

            return updatedOrder;
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

}
