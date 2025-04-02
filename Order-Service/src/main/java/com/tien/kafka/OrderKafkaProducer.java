package com.tien.kafka;

import com.tien.event.OrderEvent;
import com.tien.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderStatusUpdate(Order order) {
        OrderEvent event = new OrderEvent(order.getOrderId(), order.getStatus().name());
        kafkaTemplate.send("order-status-topic", event);
    }
}
