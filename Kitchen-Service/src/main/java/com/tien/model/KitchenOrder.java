package com.tien.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kitchen_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kitchen_orders_id;
    private String dish;
    private Integer quantity;
    private String status; // 'PENDING', 'IN_PROGRESS', 'READY', 'COMPLETED'
}
