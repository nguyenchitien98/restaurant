package com.tien.service;

import com.tien.client.MenuClient;
import com.tien.dto.event.OrderDetailEvent;
import com.tien.dto.event.OrderEvent;
import com.tien.service.kafka.OrderKafkaProducer;
import com.tien.model.*;
import com.tien.repository.OrderDetailRepository;
import com.tien.repository.OrderRepository;
import com.tien.websocket.OrderWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final TableService tableService;
    private final MenuClient menuClient;
    private final OrderWebSocketService orderWebSocketService;
    private final OrderKafkaProducer orderKafkaProducer;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;


    // Tạo mới order cho bàn
    public Order createOrder(Long tableId, Order order) {
        RestaurantTable table = tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Menu item = menuClient.getMenuById(orderDetail.getMenuId());
            if (item == null){
                throw new RuntimeException("Menu item not found: " + orderDetail.getMenuId());
            }
            orderDetail.setOrder(order);
            orderDetail.setStatus(OrderDetailStatus.PENDING);
        }

        order.setTable(table);
        order.setStatus(OrderStatus.PENDING);
        order.setUserId(order.getUserId());
        order.setOrderDetails(order.getOrderDetails());
        order.setTotalPrice(order.getOrderDetails().stream().mapToDouble(od -> od.getPrice() * od.getQuantity()).sum());

        Order savedOrder = orderRepository.save(order);

        // Chuyển OrderDetail thành danh sách sự kiện
        List<OrderDetailEvent> detailEvents = savedOrder.getOrderDetails().stream()
                .map(d -> new OrderDetailEvent(d.getOrderDetailId(), menuClient.getMenuById(d.getMenuId()).getName(), d.getQuantity()))
                .collect(Collectors.toList());

        // Gửi sự kiện tới Kitchen Service
        OrderEvent event = new OrderEvent(savedOrder.getOrderId(), detailEvents);
        kafkaTemplate.send("kitchen-orders", event);

        return savedOrder;
    }

    public void completeOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));

        orderDetail.setStatus(OrderDetailStatus.COMPLETED);
        orderDetailRepository.save(orderDetail);

        // Kiểm tra nếu tất cả món đã hoàn thành thì cập nhật Order
        Long orderId = orderDetail.getOrder().getOrderId();
        boolean allCompleted = orderDetailRepository
                .findByOrder_OrderId(orderId)
                .stream()
                .allMatch(d -> d.getStatus().equals(OrderDetailStatus.COMPLETED));

        if (allCompleted) {
            Order order = orderRepository.findById(orderId).orElseThrow();
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        }
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
//            orderKafkaProducer.sendOrderStatusUpdate(updatedOrder);

            return updatedOrder;
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

}
