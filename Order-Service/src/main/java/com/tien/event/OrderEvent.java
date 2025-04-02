package com.tien.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEvent {
    private Long orderId;
    private String status;

    public OrderEvent(Long orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

}
