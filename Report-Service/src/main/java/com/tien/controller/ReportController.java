package com.tien.controller;

import com.tien.dto.request.ReportRequest;
import com.tien.dto.response.MonthlyRevenueResponseDTO;
import com.tien.grpc.MonthlyRevenue;
import com.tien.grpc.MonthlyRevenueResponse;
import com.tien.grpc.ReportServiceGrpcClient;
import com.tien.model.RevenueReport;
import com.tien.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    private final ReportServiceGrpcClient reportServiceGrpcClient;

    @GetMapping("/revenue")
    public RevenueReport getRevenue(@RequestBody @DateTimeFormat(pattern = "yyyy-MM-dd")ReportRequest reportRequest) throws Exception {
        return reportService.generateReport(reportRequest);
    }

    @GetMapping("/monthly-revenue")
    public List<MonthlyRevenueResponseDTO> getMonthlyRevenue(@RequestParam int year) {
        return reportServiceGrpcClient.getMonthlyRevenue(year);
    }

    @GetMapping("/test-grpc")
    public ResponseEntity<?> testGrpc() {
        try {
            List<MonthlyRevenueResponseDTO> result = reportServiceGrpcClient.getMonthlyRevenue(2025);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi gọi gRPC: " + e.getMessage());
        }
    }
}
