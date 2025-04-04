package com.tien.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private String requestId; // để khớp với response
    private String reportType; // DAILY, WEEKLY, ...
    private LocalDate startDate;
    private LocalDate endDate;
}