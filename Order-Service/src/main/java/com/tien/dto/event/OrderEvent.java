package com.tien.dto.event;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private List<OrderDetailEvent> orderDetails;

}
