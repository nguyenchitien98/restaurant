package com.tien.service.kafka;

import com.tien.dto.event.OrderEvent;
import com.tien.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KitchenListener {
    private final KitchenService kitchenService;

    @KafkaListener(topics = "kitchen-orders", groupId = "kitchen-group")
    public void listenOrder(OrderEvent event) {
        System.out.println("Kitchen received order: " + event);
        kitchenService.processOrder(event);
    }
}
