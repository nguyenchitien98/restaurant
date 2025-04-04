package com.tien.service.kafka;

import com.tien.dto.event.OrderEventStatus;
import com.tien.model.OrderStatus;
import com.tien.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaConsumer {
    private final InvoiceService invoiceService;

    @KafkaListener(topics = "order-status-topic", groupId = "invoice-group")
    public void consumeOrderEvent(OrderEventStatus event) {
        if (OrderStatus.COMPLETED.equals(event.getStatus())) {
            invoiceService.createInvoice(event.getOrderId(), event.getTotalAmount());
        }
    }
}
