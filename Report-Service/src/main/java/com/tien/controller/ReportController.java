package com.tien.controller;

import com.tien.dto.request.ReportRequest;
import com.tien.model.RevenueReport;
import com.tien.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/revenue")
    public RevenueReport getRevenue(@RequestBody @DateTimeFormat(pattern = "yyyy-MM-dd")ReportRequest reportRequest) throws Exception {
        return reportService.generateReport(reportRequest);
    }
}
