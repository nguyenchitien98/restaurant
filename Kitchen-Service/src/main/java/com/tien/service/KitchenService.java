package com.tien.service;

import com.tien.dto.event.OrderDetailEvent;
import com.tien.dto.event.OrderEvent;
import com.tien.model.KitchenOrder;
import com.tien.repository.KitchenOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KitchenService {
    private final KitchenOrderRepository kitchenOrderRepository;
    private final KafkaTemplate<String, OrderDetailEvent> kafkaTemplate;

    public void processOrder(OrderEvent event) {
        for (OrderDetailEvent detailEvent : event.getOrderDetails()) {
            KitchenOrder kitchenOrder = new KitchenOrder(
                    detailEvent.getOrderDetailId(),
                    detailEvent.getDish(),
                    detailEvent.getQuantity(),
                    "IN_PROGRESS"
            );
            kitchenOrderRepository.save(kitchenOrder);

            // Giả lập chế biến món ăn (2s)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Cập nhật trạng thái hoàn thành
            kitchenOrder.setStatus("COMPLETED");
            kitchenOrderRepository.save(kitchenOrder);

            // Gửi sự kiện món ăn hoàn thành
            kafkaTemplate.send("order-detail-completed", detailEvent);
        }
    }
}
