package com.tien.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueRequest {
    private String reportType;
    private LocalDate start;
    private LocalDate end;
}
