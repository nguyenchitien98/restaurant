//package com.tien.service.kafka;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class MenuNameKafkaConsumer {
//    private final MenuCache menuCache;
//
//    // Lắng nghe response từ MenuService
//    @KafkaListener(topics = "menu-name-response", groupId = "order-service")
//    public void handleMenuResponse(Map<Long, String> menuMap) {
//        menuCache.update(menuMap);
//    }
//
//}
