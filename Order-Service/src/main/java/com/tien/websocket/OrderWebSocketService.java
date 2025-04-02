package com.tien.websocket;

import com.tien.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderWebSocketService {
   private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderUpdate(Order order) {
        messagingTemplate.convertAndSend("/topic/order-status", order);
    }
}
