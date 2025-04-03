package com.tien.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailEvent {
    private Long orderDetailId;
    private String dish;
    private Integer quantity;
}
