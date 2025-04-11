package com.tien.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Double totalAmount;
    private LocalDate createdAt = LocalDate.now();
}
