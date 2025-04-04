package com.tien.service.kafka;

import com.tien.dto.event.ReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportRequestProducer {
    private final KafkaTemplate<String, ReportRequest> kafkaTemplate;

    public void sendReportRequest(String requestId,String reportType, LocalDate startDate, LocalDate endDate) {
        ReportRequest request = new ReportRequest();
        request.setRequestId(requestId);
        request.setReportType(reportType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        kafkaTemplate.send("report-request", request);
    }
}
