package com.tien.model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReport {
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private Integer totalInvoices;
}
