package com.tien.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    private Long menuId;
    private Integer quantity;
    private Double price;
    private String status; // PENDING, IN_PROGRESS, COMPLETED

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}