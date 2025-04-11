package com.tien.dto.event;

import com.tien.model.Invoices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private String requestId;
    private String reportType;
    private List<Invoices> invoices;
}
