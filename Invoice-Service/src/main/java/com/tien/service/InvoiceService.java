package com.tien.service;

import com.tien.model.Invoice;
import com.tien.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Long orderId, Double totalAmount) {
        Invoice invoice = new Invoice();
        invoice.setOrderId(orderId);
        invoice.setTotalAmount(totalAmount);
        return invoiceRepository.save(invoice);

//        invoiceWebSocketService.sendInvoiceUpdate(savedInvoice);
//        return savedInvoice;
    }

    public Invoice getInvoiceByOrderId(Long orderId) {
        Optional<Invoice> invoice = invoiceRepository.findByOrderId(orderId);
        return invoice.orElse(null);
    }
}
