package com.tien.service.kafka;

import com.tien.dto.event.InvoiceResponse;
import com.tien.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InvoiceKafkaConsumer {

    private final Map<String, CompletableFuture<List<Invoice>>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<List<Invoice>> waitForResponse(String requestId) {
        CompletableFuture<List<Invoice>> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        return future;
    }

    @KafkaListener(topics = "invoice-response", groupId = "report-group")
    public void listenInvoiceResponse(InvoiceResponse response) {
        CompletableFuture<List<Invoice>> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response.getInvoices());
        }
    }

}
