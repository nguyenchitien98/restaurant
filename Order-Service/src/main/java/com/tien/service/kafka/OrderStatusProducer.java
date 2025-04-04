package com.tien.service.kafka;

import com.tien.dto.event.OrderEventStatus;
import com.tien.model.Order;
import com.tien.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusProducer {
    private final KafkaTemplate<String, Object> genericKafkaTemplate;
    private final OrderRepository orderRepository;

    public void sendOrderStatusToInvoice(OrderEventStatus orderEventStatus) {
        Order order = orderRepository.findById(orderEventStatus.getOrderId()).orElse(null);
        orderEventStatus.setOrderId(order.getOrderId());
        orderEventStatus.setStatus(orderEventStatus.getStatus());
        orderEventStatus.setTotalAmount(order.getTotalPrice());
        genericKafkaTemplate.send("order-status-topic", orderEventStatus);
    }
}
