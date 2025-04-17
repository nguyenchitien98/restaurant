package com.tien.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.dto.event.MenuIdRequestEvent;
import com.tien.model.Menu;
import com.tien.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuNameKafkaListener {

    private final MenuRepository menuRepository;
    private final KafkaTemplate<String, Map<Long, String>> kafkaTemplate;

    @KafkaListener(topics = "menu-name-request", groupId = "menu-service")
    public void handleMenuNameRequest(MenuIdRequestEvent menuIdRequestEvent) {
        System.out.println(menuIdRequestEvent);
        List<Long> menuIds = menuIdRequestEvent.getMenuIds().stream()
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    } else if (obj instanceof Long) {
                        return (Long) obj;
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
                    }
                })
                .collect(Collectors.toList());

        Map<Long, String> response = new HashMap<>();

        for (Long id : menuIds) {
            Menu menu = menuRepository.findById(id).orElse(null);
            if (menu != null) {
                response.put(id, menu.getName());
            }
        }

        kafkaTemplate.send("menu-name-response", response);
    }
}
