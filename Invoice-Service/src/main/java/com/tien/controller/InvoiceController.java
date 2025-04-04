package com.tien.controller;

import com.tien.model.Invoice;
import com.tien.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable Long orderId) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }
}
