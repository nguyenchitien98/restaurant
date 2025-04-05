package com.tien.service;

import com.tien.model.Invoice;
import com.tien.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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

    public List<Invoice> getInvoicesByDateRange(LocalDate start, LocalDate end) {
        return invoiceRepository.findByCreatedAtBetween(start,end);
    }

    public List<Invoice> getInvoicesByDay(LocalDate date) {
        return invoiceRepository.getRevenueByDay(date);
    }

    public List<Invoice> getInvoicesByDayRange(LocalDate start, LocalDate end) {
        return invoiceRepository.getRevenueByDayRange(start, end);
    }

    public List<Invoice> getInvoicesByMonth(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getRevenueByMonth(startDate, endDate);
    }

    public List<Invoice> getInvoicesByYear(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getRevenueByYear(startDate, endDate);
    }
}
