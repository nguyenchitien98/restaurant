package com.tien.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM(PENDING,CONFIRMED,COOKING,COMPLETED,CANCELLED")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "table_id")
    @JsonIgnore
    private RestaurantTable table; // Mỗi order sẽ liên kết với một bàn

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails; // Chi tiết món ăn trong order
}