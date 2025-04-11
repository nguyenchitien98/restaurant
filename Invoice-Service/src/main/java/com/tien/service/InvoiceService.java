package com.tien.service;

import com.tien.model.Invoices;
import com.tien.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final KafkaTemplate<String, Object> genericKafkaTemplate;

    public Invoices createInvoice(Long orderId, Double totalAmount) {
        Invoices invoice = new Invoices();
        invoice.setOrderId(orderId);
        invoice.setTotalAmount(totalAmount);
        return invoiceRepository.save(invoice);

//        invoiceWebSocketService.sendInvoiceUpdate(savedInvoice);
//        return savedInvoice;
    }

    // API này thực hiện cho phần test Websocket
    public Invoices createInvoice1(Long orderId, Double totalAmount) {
        Invoices invoice = new Invoices();
        invoice.setOrderId(orderId);
        invoice.setTotalAmount(totalAmount);
        Invoices savedInvoice = invoiceRepository.save(invoice);
        Double total = invoiceRepository.getTotalRevenue();
        System.out.println("amount = " + total);
        genericKafkaTemplate.send("revenue-topic", total);

        return savedInvoice;
    }

    public Invoices getInvoiceByOrderId(Long orderId) {
        Optional<Invoices> invoice = invoiceRepository.findByOrderId(orderId);
        return invoice.orElse(null);
    }

    public List<Invoices> getInvoicesByDateRange(LocalDate start, LocalDate end) {
        return invoiceRepository.findByCreatedAtBetween(start,end);
    }

    public List<Invoices> getInvoicesByDay(LocalDate date) {
        return invoiceRepository.getRevenueByDay(date);
    }

    public List<Invoices> getInvoicesByDayRange(LocalDate start, LocalDate end) {
        return invoiceRepository.getRevenueByDayRange(start, end);
    }

    public List<Invoices> getInvoicesByMonth(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getRevenueByMonth(startDate, endDate);
    }

    public List<Invoices> getInvoicesByYear(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getRevenueByYear(startDate, endDate);
    }
}
