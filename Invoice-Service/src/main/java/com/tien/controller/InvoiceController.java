package com.tien.controller;

import com.tien.dto.request.InvoiceRequest;
import com.tien.model.Invoices;
import com.tien.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
