package com.tien.service.kafka;

import com.tien.dto.event.InvoiceResponse;
import com.tien.dto.event.ReportRequest;
import com.tien.model.Invoice;
import com.tien.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportRequestConsumerService {
    private final KafkaTemplate<String, InvoiceResponse> kafkaTemplate;
    private final InvoiceService invoiceService;

    @KafkaListener(topics = "report-request", groupId = "invoice-group")
    public void listenReportRequest(ReportRequest request) {
        List<Invoice> invoices = invoiceService.getInvoicesByDateRange(request.getStartDate(), request.getEndDate());

        InvoiceResponse response = new InvoiceResponse();
        response.setRequestId(request.getRequestId()); // Để khớp request
        response.setReportType(request.getReportType());
        response.setInvoices(invoices);

        kafkaTemplate.send("invoice-response", response);
    }
}
