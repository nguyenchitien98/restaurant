package com.tien.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @Column(columnDefinition = "ENUM('OCCUPIED', 'RESERVED', 'EMPTY') DEFAULT 'EMPTY'")
    private TableStatus status; // Trạng thái: EMPTY:TRỐNG, OCCUPIED: ĐÃ CÓ KHÁCH, RESERVED: ĐẶT TRƯỚC, v.v.
    private Integer capacity;

    private String note;

    private LocalDateTime reserved_at;

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Order> orders;

}
