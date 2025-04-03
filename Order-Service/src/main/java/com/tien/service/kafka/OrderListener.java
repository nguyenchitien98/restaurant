package com.tien.service.kafka;

import com.tien.dto.event.OrderDetailEvent;
import com.tien.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderListener {
    private final OrderService orderService;

    public OrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-detail-completed", groupId = "order-group")
    public void listenOrderDetailCompletion(OrderDetailEvent event) {
        System.out.println("Order detail completed: " + event.getOrderDetailId());
        orderService.completeOrderDetail(event.getOrderDetailId());
    }
}
