package com.tien.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long table_id;

    private Integer table_number; // Tên bàn (ví dụ: Bàn 1, Bàn 2, ...)

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED', 'EMPTY') DEFAULT 'AVAILABLE'")
    private TableStatus status; // Trạng thái: TRỐNG, ĐÃ CÓ KHÁCH, ĐẶT TRƯỚC, v.v.
    private Integer capacity;

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Order> orders;

}
