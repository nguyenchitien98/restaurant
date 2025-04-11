package com.tien.dto.request;

import lombok.Data;

@Data
public class InvoiceRequest {
    private Long orderId;
    private Double totalAmount;
}
