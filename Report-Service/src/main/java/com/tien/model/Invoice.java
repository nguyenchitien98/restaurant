package com.tien.model;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    private Long id;

    private Long orderId;
    private Double totalAmount;
    private LocalDate createdAt = LocalDate.now();
}

