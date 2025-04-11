package com.tien.controller;

import com.tien.dto.request.InvoiceRequest;
import com.tien.dto.response.WeeklyRevenueDTO;
import com.tien.model.Invoices;
import com.tien.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Invoices> getInvoiceByOrderId(@PathVariable Long orderId) {
        Invoices invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping
    public ResponseEntity<Invoices> createInvoice1(@RequestBody InvoiceRequest invoice) {
        Invoices newInvoice = invoiceService.createInvoice1(invoice.getOrderId(), invoice.getTotalAmount());
        return ResponseEntity.ok(newInvoice);
    }

    @GetMapping("/weekly-revenue")
    public List<WeeklyRevenueDTO> getWeeklyRevenue() {
        return invoiceService.getWeeklyRevenue();
    }

    // API: Lấy tổng doanh thu của tuần hiện tại
    @GetMapping("/weekly-total")
    public Double getWeeklyTotalRevenue() {
        return invoiceService.getTotalRevenueWeekly();
    }
}
